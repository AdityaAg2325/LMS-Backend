package com.example.lms.dto;

import lombok.Data;

@Data
public class BookOutDTO {

    private Long id;

    private String title;

    private String author;

    private String image;

    private Long quantity;

    private String categoryName;
}
