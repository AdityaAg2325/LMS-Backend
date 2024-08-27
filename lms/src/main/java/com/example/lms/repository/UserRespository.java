package com.example.lms.repository;

import com.example.lms.entity.Book;
import com.example.lms.entity.Category;
import com.example.lms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRespository extends JpaRepository<User, Long> {

    Page<User> findByRole(String Role, Pageable pageable);

    User findByMobileNumber(String mobileNumber);

    Page<User> findAll(Pageable pageable);

    Page<User> findByMobileNumberContainingIgnoreCaseAndRole(String mobileNumber, String role, Pageable pageable);
}
