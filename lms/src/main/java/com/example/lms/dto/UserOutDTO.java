package com.example.lms.dto;

import lombok.Data;

@Data
public class UserOutDTO {

    private Long id;

    private String name;

    private String mobileNumber;

    private String email;

    private String role;

    private String token;
}
