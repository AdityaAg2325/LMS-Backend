package com.example.lms.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {

        private String name;

        private String email;

        private String mobileNumber;

        private String role = "ROLE_USER";

        private String password = "";
}
