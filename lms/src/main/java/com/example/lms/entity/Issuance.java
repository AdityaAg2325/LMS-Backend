package com.example.lms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data @Table(name = "issuances") @Entity
@NoArgsConstructor @AllArgsConstructor
public class Issuance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book", nullable = false)
    private Book book;

    @Column(name = "issue_time", nullable = false)
    private LocalDateTime issueTime;

    @Column(name = "return_time")
    private LocalDateTime returnTime;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String type;

    @PrePersist
    protected void onCreate(){
        this.issueTime = LocalDateTime.now();
    }
}



