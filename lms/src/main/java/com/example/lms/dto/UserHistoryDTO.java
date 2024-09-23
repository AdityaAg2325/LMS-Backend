package com.example.lms.dto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class UserHistoryDTO {

    private Long id;

    private BookOutDTO book;

    private LocalDateTime issueTime;

    private LocalDateTime expectedReturnTime;

    private LocalDateTime actualReturnTime;

    private String status;

    private String type;

}
