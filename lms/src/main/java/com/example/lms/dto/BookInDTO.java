package com.example.lms.dto;

import lombok.Data;

@Data
public class BookInDTO {

    private String title;

    private String author;

    private Long quantity;

    private String image;

    private Long categoryId;
}
