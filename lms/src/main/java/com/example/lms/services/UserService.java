package com.example.lms.services;

import com.example.lms.dto.BookOutDTO;
import com.example.lms.dto.UserOutDTO;
import com.example.lms.dto.UserRegisterDTO;
import com.example.lms.entity.Book;
import com.example.lms.mapper.UserMapper;
import com.example.lms.repository.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.lms.entity.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRespository userRespository;

    @Autowired
    private UserMapper userMapper;

    public UserOutDTO getUserByMobile(String mobileNumber){
        User user = userRespository.findByMobileNumber(mobileNumber);

        UserOutDTO userOutDTO = userMapper.toDTO(user);
        return userOutDTO;
    }

    public UserOutDTO deleteUserByMobile(String mobileNumber){
        User user = userRespository.findByMobileNumber(mobileNumber);

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

        User user = userRespository.findByMobileNumber(mobileNumber);

        user.setMobileNumber(userRegisterDTO.getMobileNumber());
        user.setName(userRegisterDTO.getName());
        user.setEmail(userRegisterDTO.getEmail());
        User updated = userRespository.save(user);

        UserOutDTO userOutDTO = userMapper.toDTO(updated);
        return userOutDTO;
    }

    public UserOutDTO createUser(UserRegisterDTO userRegisterDTO){
        User user = userMapper.toEntity(userRegisterDTO);
        User savedUser = userRespository.save(user);
        UserOutDTO userOutDTO = userMapper.toDTO(savedUser);
        return userOutDTO;
    }
}
