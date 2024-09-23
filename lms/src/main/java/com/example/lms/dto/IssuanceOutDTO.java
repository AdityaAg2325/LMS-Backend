package com.example.lms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IssuanceOutDTO {

    private Long id;
    private UserOutDTO user;
    private BookOutDTO book;
    private LocalDateTime issueTime;
    private LocalDateTime expectedReturnTime;
    private LocalDateTime actualReturnTime;
    private String status;
    private String type;
}
