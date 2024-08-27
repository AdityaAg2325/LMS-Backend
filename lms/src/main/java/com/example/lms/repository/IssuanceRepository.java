package com.example.lms.repository;

import com.example.lms.entity.Issuance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IssuanceRepository extends JpaRepository<Issuance, Long> {

    List<Issuance> findAllByUserId(Long userId);

    List<Issuance> findAllByIssueTime(LocalDateTime issueTime);

    List<Issuance> findAllByReturnTime(LocalDateTime returnTime);
}

