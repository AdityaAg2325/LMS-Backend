package com.example.lms.repository;

import com.example.lms.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findCategoryById(Long id);

    Page<Category> findAll(Pageable pageable);

    Optional<Category> findByName(String name);

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
