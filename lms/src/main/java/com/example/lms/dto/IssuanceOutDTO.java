package com.example.lms.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class IssuanceOutDTO {

    private Long id;
    private UserOutDTO user;
    private BookOutDTO book;
    private LocalDateTime issueTime;
    private LocalDateTime returnTime;
    private String status;
    private String type;
}
