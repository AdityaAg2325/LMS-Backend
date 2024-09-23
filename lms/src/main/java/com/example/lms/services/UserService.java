package com.example.lms.services;

import com.example.lms.constants.JWTConstants;
import com.example.lms.dto.BookOutDTO;
import com.example.lms.dto.UserOutDTO;
import com.example.lms.dto.UserRegisterDTO;
import com.example.lms.entity.Book;
import com.example.lms.exception.ResourceAlreadyExistsException;
import com.example.lms.mapper.UserMapper;
import com.example.lms.repository.IssuanceRepository;
import com.example.lms.repository.UserRespository;
import com.example.lms.utils.PasswordGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.lms.entity.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRespository userRespository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SMSService smsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IssuanceRepository issuanceRepository;

    @Autowired
    private Environment env;

    public UserOutDTO getUserByMobile(String mobileNumber){
        User user = userRespository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new UsernameNotFoundException("User not found for " + mobileNumber)
        );

        UserOutDTO userOutDTO = userMapper.toDTO(user);
        return userOutDTO;
    }

    public UserOutDTO getUserByEmail(String email){
        User user = userRespository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found for "+email)
        );

        UserOutDTO userOutDTO = userMapper.toDTO(user);
        return userOutDTO;
    }

    public UserOutDTO deleteUserByMobile(String mobileNumber){
        User user = userRespository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new UsernameNotFoundException("User details not found for this mobile number - " + mobileNumber)
        );

        boolean isBookIssued = issuanceRepository.existsByUserIdAndStatus(user.getId(), "Issued");
        if(isBookIssued){
            throw new IllegalStateException("This user has some books issued so it can't be deleted!");
        }
        issuanceRepository.deleteAllByUserIn(Collections.singletonList(user));
        userRespository.deleteById(user.getId());
        UserOutDTO userOutDTO = userMapper.toDTO(user);
        return userOutDTO;
    }

    public List<UserOutDTO> getAllUsers(){
        List<User> users = userRespository.findAll();
        List<UserOutDTO> userDTOs = new ArrayList<>();
        for(User user: users){
            userDTOs.add(userMapper.toDTO(user));
        }
        return userDTOs;
    }

    public Long countAllUsers(){
        Long total = userRespository.count();
        return total;
    }

    public Page<UserOutDTO> getUsersPaginated(int pageNumber, int pageSize, String sortBy, String sortDir, String search){
        Page<User> page;
        if(search!=null && !search.isEmpty()){
            page = userRespository.findByMobileNumberContainingIgnoreCaseAndRole(search,"ROLE_USER", PageRequest.of(pageNumber,pageSize));
        } else {
            page = userRespository.findByRole( "ROLE_USER", PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(sortDir), sortBy));
        }
        return page.map(user -> userMapper.toDTO(user));
    }

    public UserOutDTO updateUserByMobile(String mobileNumber, UserRegisterDTO userRegisterDTO){

        User user = userRespository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new UsernameNotFoundException("User details not found for this mobile number - " + userRegisterDTO.getMobileNumber())
        );

        Optional<User> optionalUser = userRespository.findByMobileNumber(userRegisterDTO.getMobileNumber());
        if (optionalUser.isPresent()) {
            User thisUser = optionalUser.get();
            if (thisUser.getId() != user.getId()) {
                throw new ResourceAlreadyExistsException("User already exists for this mobile number - " + userRegisterDTO.getMobileNumber());
            }
        }

        optionalUser = userRespository.findByEmail(userRegisterDTO.getEmail());
        if (optionalUser.isPresent()) {
            User thisUser = optionalUser.get();
            if (thisUser.getId() != user.getId()) {
                throw new ResourceAlreadyExistsException("User already exists for this email - " + userRegisterDTO.getEmail());
            }
        }

        user.setMobileNumber(userRegisterDTO.getMobileNumber());
        user.setName(userRegisterDTO.getName());
        user.setEmail(userRegisterDTO.getEmail());
        if (userRegisterDTO.getPassword() != null && userRegisterDTO.getPassword().length() >= 3) {
            String encodedPassword = userRegisterDTO.getPassword();
            byte[] decodedBytes = Base64.getDecoder().decode(encodedPassword);
            String decodedPassword = new String(decodedBytes);
            userRegisterDTO.setPassword(decodedPassword);
            user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }
        User updated = userRespository.save(user);

        UserOutDTO userOutDTO = userMapper.toDTO(updated);
        return userOutDTO;
    }

    public UserOutDTO createUser(UserRegisterDTO userRegisterDTO){
        Optional<User> optionalUser = userRespository.findByMobileNumber(userRegisterDTO.getMobileNumber());
        if(optionalUser.isPresent()){
            throw new ResourceAlreadyExistsException("User with this mobile number already exist - " + userRegisterDTO.getMobileNumber());
        }

        optionalUser = userRespository.findByEmail(userRegisterDTO.getEmail());
        if(optionalUser.isPresent()){
            throw new ResourceAlreadyExistsException("User with this Email aleready exist - " + userRegisterDTO.getEmail());
        }

        User user = userMapper.toEntity(userRegisterDTO);

        String newPassword = PasswordGenerator.generatePassword(10);
        user.setPassword(passwordEncoder.encode(newPassword));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }
        User savedUser = userRespository.save(user);

        String message = String.format( "\nWelcome %s\n" +
                        "You have been successfully registered to Page Palace\n" +
                        "Below are your login details\n" +
                        "Username: %s (AND) %s\n" +
                        "Password: %s",
                savedUser.getName(),
                savedUser.getMobileNumber(),
                savedUser.getEmail(),
                newPassword);

        smsService.sendSms(savedUser.getMobileNumber(), message);

        UserOutDTO userOutDTO = userMapper.toDTO(savedUser);
        return userOutDTO;
    }

    public UserOutDTO getUserByToken(String jwt) {
        String secret = env.getProperty(JWTConstants.JWT_SECRET_KEY, JWTConstants.JWT_SECRET_DEFAULT_VALUE);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().verifyWith(secretKey)
                .build().parseSignedClaims(jwt).getPayload();
        String username = String.valueOf(claims.get("username"));

        if (username.contains("@")) {
//                            email
            User user = userRespository.findByEmail(username).orElseThrow(
                    () -> new BadCredentialsException("Invalid Token received!")
            );

            UserOutDTO userDTO = UserMapper.toDTO(user);
            userDTO.setToken(jwt);
            return  userDTO;
        } else {
//                            mobile
            User user = userRespository.findByMobileNumber(username).orElseThrow(
                    () -> new BadCredentialsException("Invalid Token received!")
            );

            UserOutDTO userDTO = UserMapper.toDTO(user);
            userDTO.setToken(jwt);
            return  userDTO;
        }

    }
}
