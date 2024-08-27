package com.example.lms.repository;

import com.example.lms.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Book, Long> {
    Book findBookById(Long id);
    Book findBookByTitle(String title);
    List<Book> findBooksByCategoryName(String categoryName);

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Book> findAll(Pageable pageable);
}
