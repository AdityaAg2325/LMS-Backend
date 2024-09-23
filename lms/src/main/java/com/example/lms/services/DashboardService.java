package com.example.lms.services;

import com.example.lms.dto.DashboardData;
import com.example.lms.repository.BooksRepository;
import com.example.lms.repository.CategoryRepository;
import com.example.lms.repository.IssuanceRepository;
import com.example.lms.repository.UserRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    @Autowired
            private CategoryRepository categoryRepository;
    @Autowired
            private IssuanceRepository issuanceRepository;
    @Autowired
            private BooksRepository booksRepository;
    @Autowired
            private UserRespository userRespository;

    public DashboardData showStats() {

        DashboardData dashboardData = new DashboardData();

        dashboardData.setTotalCategories(categoryRepository.count());
        dashboardData.setCurrentIssuances(issuanceRepository.countDistinctUsersByStatusAndIssuanceType("Issued", "In house"));
        dashboardData.setTotalBooks(booksRepository.count());
        Long totalUser = userRespository.count() - userRespository.findByRole("ROLE_ADMIN").size();
        dashboardData.setTotalUsers(totalUser);

        return dashboardData;
    }
}
