package com.example.lms.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "books") @Data
@NoArgsConstructor @AllArgsConstructor
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(nullable = false)
    private Long quantity = 1L;

    @Column(name = "curr_qty", nullable = false)
    private Long currQty = 0L;

    private String image;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;
}
