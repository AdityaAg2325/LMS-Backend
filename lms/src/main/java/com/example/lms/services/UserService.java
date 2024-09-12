package com.example.lms.services;

import com.example.lms.constants.JWTConstants;
import com.example.lms.dto.BookOutDTO;
import com.example.lms.dto.UserOutDTO;
import com.example.lms.dto.UserRegisterDTO;
import com.example.lms.entity.Book;
import com.example.lms.mapper.UserMapper;
import com.example.lms.repository.UserRespository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.lms.entity.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRespository userRespository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
                () -> new UsernameNotFoundException("User not found for " + mobileNumber)
        );;

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

    public Page<UserOutDTO> getUsersPaginated(int pageNumber, int pageSize, String search){
        Page<User> page;
        if(search!=null && !search.isEmpty()){
            page = userRespository.findByMobileNumberContainingIgnoreCaseAndRole(search,"ROLE_USER", PageRequest.of(pageNumber,pageSize));
        } else {
            page = userRespository.findByRole( "ROLE_USER", PageRequest.of(pageNumber,pageSize));
        }
        return page.map(user -> userMapper.toDTO(user));
    }

    public UserOutDTO updateUserByMobile(String mobileNumber, UserRegisterDTO userRegisterDTO){

        User user = userRespository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new UsernameNotFoundException("User not found for mobile no. " + mobileNumber)
        );;

        user.setMobileNumber(userRegisterDTO.getMobileNumber());
        user.setName(userRegisterDTO.getName());
        user.setEmail(userRegisterDTO.getEmail());
        User updated = userRespository.save(user);

        UserOutDTO userOutDTO = userMapper.toDTO(updated);
        return userOutDTO;
    }

    public UserOutDTO createUser(UserRegisterDTO userRegisterDTO){
        User user = userMapper.toEntity(userRegisterDTO);
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        User savedUser = userRespository.save(user);
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
