package com.example.lms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IssuanceInDTO {

    private Long userId;
    private Long bookId;
    private LocalDateTime returnTime;
    private String status;
    private String type;
}
