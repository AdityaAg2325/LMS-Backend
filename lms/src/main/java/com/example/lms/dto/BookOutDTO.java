package com.example.lms.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class BookOutDTO {

    private Long id;

    private String title;

    private String author;

    private String image;

    private Long quantity;

    private Long currQty;

    private String categoryName;
}
