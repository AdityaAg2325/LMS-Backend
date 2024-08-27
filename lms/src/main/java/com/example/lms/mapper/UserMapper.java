package com.example.lms.mapper;

import com.example.lms.entity.User;
import com.example.lms.dto.UserLoginDTO;
import com.example.lms.dto.UserRegisterDTO;
import com.example.lms.dto.UserOutDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static User toEntity(UserRegisterDTO registerDTO){
        User user = new User();
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setMobileNumber(registerDTO.getMobileNumber());
        user.setPassword(registerDTO.getPassword());
        user.setRole(registerDTO.getRole());
        return user;
    }


    public static UserOutDTO toDTO(User user){
        UserOutDTO outDTO = new UserOutDTO();
        outDTO.setId(user.getId());
        outDTO.setName(user.getName());
        outDTO.setMobileNumber(user.getMobileNumber());
        outDTO.setEmail(user.getEmail());
        outDTO.setRole(user.getRole());
        return outDTO;
    }
}
