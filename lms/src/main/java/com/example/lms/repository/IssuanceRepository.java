package com.example.lms.repository;

import com.example.lms.entity.Book;
import com.example.lms.entity.Category;
import com.example.lms.entity.Issuance;
import com.example.lms.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IssuanceRepository extends JpaRepository<Issuance, Long> {

    List<Issuance> findAllByUserId(Long userId);

    List<Issuance> findAllByIssueTime(LocalDateTime issueTime);

    List<Issuance> findAllByActualReturnTime(LocalDateTime returnTime);

    List<Issuance> findByStatus(String status);

    Page<Issuance> findAll(Pageable pageable);

    @Query("SELECT i FROM Issuance i WHERE i.book.id = :id")
    List<Issuance> findAllByBookId(Long id);

    @Query("SELECT COUNT(DISTINCT i.user) FROM Issuance i WHERE i.status = 'ISSUED'")
    Long countDistinctUsersByStatus(String status);


//    ------------------------------------------------

    Page<Issuance> findAllByUserId(Long id, Pageable pageable);

    List<Issuance> findAllByExpectedReturnTimeBetweenAndStatus(LocalDateTime start, LocalDateTime end, String status);

    Page<Issuance> findByBookContainingIgnoreCase(String book, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT i.user.id) FROM Issuance i WHERE i.status = :status AND i.type = :type")
    Long countDistinctUsersByStatusAndIssuanceType(@Param("status") String status, @Param("type") String type);

    @Query("SELECT COUNT(DISTINCT i.user.id) FROM Issuance i WHERE i.type = 'In house' AND i.status = 'Issued' AND i.issueTime BETWEEN :startOfDay AND :endOfDay")
    Long countDistinctUsersInLibraryToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);


    Page<Issuance> findByUserContainingIgnoreCase(
            String user,
            Pageable pageable);


    Page<Issuance> filterIssuances(Pageable pageable);

    boolean existsByBookCategoryIdAndStatus(Long categoryId, String status);

    @Modifying
    @Transactional
    void deleteAllByBookIn(List<Book> books);

    @Modifying
    @Transactional
    void deleteAllByUserIn(List<User> users);

    boolean existsByBookIdAndStatus(Long bookId, String status);
    boolean existsByUserIdAndStatus(Long userId, String status);
}

