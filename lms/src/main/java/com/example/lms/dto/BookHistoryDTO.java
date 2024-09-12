package com.example.lms.dto;

import com.example.lms.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@RequiredArgsConstructor
public class BookHistoryDTO {

    private Long id;

    private User user;

    private LocalDateTime issueTime;

    private LocalDateTime expectedReturnTime;

    private LocalDateTime actualReturnTime;

    private String status;

    private String type;

}
