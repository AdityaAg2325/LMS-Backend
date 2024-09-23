package com.example.lms.repository;

import com.example.lms.entity.Book;
import com.example.lms.entity.Category;
import com.example.lms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRespository extends JpaRepository<User, Long> {

    Page<User> findByRole(String Role, Pageable pageable);
    List<User> findByRole(String role, Sort sort);
    Optional<User> findByMobileNumber(String mobileNumber);
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);

    Page<User> findAll(Pageable pageable);

    Page<User> findByMobileNumberContainingIgnoreCaseAndRole(String mobileNumber, String role, Pageable pageable);
}
