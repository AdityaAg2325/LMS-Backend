package com.example.lms.dto;

import lombok.Data;

@Data
public class DashboardData {
    private Long totalBooks;
    private Long totalCategories;
    private Long currentIssuances;
    private Long totalUsers;
}
