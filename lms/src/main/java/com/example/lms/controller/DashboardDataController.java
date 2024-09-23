package com.example.lms.controller;

import com.example.lms.dto.DashboardData;
import com.example.lms.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping(path="/api/stats")
public class DashboardDataController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/statistics")
    public ResponseEntity<DashboardData> getStatistics() {
        DashboardData dashboardData = dashboardService.showStats();
        return ResponseEntity.status(HttpStatus.OK).body(dashboardData);
    }
}
