package com.example.lms.repository;

import com.example.lms.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Book, Long> {
    Optional<Book> findBookById(Long id);

    Optional<Book> findBookByTitle(String title);
    Optional<Book> findByTitle(String title);
    @Query("SELECT b FROM Book b WHERE b.category.id = :id")
    List<Book> findAllByCategory(Long id);

    @Query("SELECT SUM(b.quantity) FROM Book b")
    Long getTotalBooksCount();
    @Query("SELECT SUM(b.currQty) FROM Book b")
    Long getTotalAvailableBooks();
    List<Book> findBooksByCategoryName(String categoryName);

    Long countByCurrQtyGreaterThan(int qty);
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Book> findAll(Pageable pageable);

    @Modifying
    @Query("DELETE FROM Book b WHERE b.category.id = :categoryId")
    void deleteAllByCategory(@Param("categoryId") Long categoryId);
}
