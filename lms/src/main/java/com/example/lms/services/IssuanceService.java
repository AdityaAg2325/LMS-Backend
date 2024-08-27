package com.example.lms.services;

import com.example.lms.dto.IssuanceInDTO;
import com.example.lms.dto.IssuanceOutDTO;
import com.example.lms.entity.Book;
import com.example.lms.entity.Issuance;
import com.example.lms.entity.User;
import com.example.lms.mapper.IssuanceMapper;
import com.example.lms.repository.BooksRepository;
import com.example.lms.repository.IssuanceRepository;
import com.example.lms.repository.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IssuanceService {

    @Autowired
    private IssuanceRepository issuanceRepository;

    @Autowired
    private UserRespository userRepository;

    @Autowired
    private BooksRepository bookRepository;

    @Autowired
    private IssuanceMapper issuanceMapper;

    public IssuanceOutDTO createIssuance(IssuanceInDTO inDTO) {
        User user = userRepository.findById(inDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(inDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Issuance issuance = IssuanceMapper.toEntity(inDTO, user, book);
        issuance = issuanceRepository.save(issuance);

        return IssuanceMapper.toDTO(issuance);
    }

    public IssuanceOutDTO getIssuanceById(Long id) {
        Issuance issuance = issuanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issuance not found"));
        return IssuanceMapper.toDTO(issuance);
    }

    public void deleteIssuanceById(Long id) {
        issuanceRepository.deleteById(id);
    }

    public IssuanceOutDTO updateIssuance(Long id, IssuanceInDTO inDTO) {
        Issuance issuance = issuanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issuance not found"));

        User user = userRepository.findById(inDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(inDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        issuance.setUser(user);
        issuance.setBook(book);
        issuance.setIssueTime(inDTO.getIssueTime());
        issuance.setReturnTime(inDTO.getReturnTime());
        issuance.setStatus(inDTO.getStatus());
        issuance.setType(inDTO.getType());

        issuance = issuanceRepository.save(issuance);
        return IssuanceMapper.toDTO(issuance);
    }

    public IssuanceOutDTO updateStatus(Long id, IssuanceInDTO issuanceInDTO) {
        Issuance issuance = issuanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issuance not found"));

        issuance.setStatus(issuanceInDTO.getStatus());
        issuance = issuanceRepository.save(issuance);

        return IssuanceMapper.toDTO(issuance);
    }

    public List<IssuanceOutDTO> getAllIssuanceByUserId(Long userId) {
        List<Issuance> issuances = issuanceRepository.findAllByUserId(userId);
        return issuances.stream()
                .map(IssuanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<IssuanceOutDTO> getAllIssuanceByMobile(String mobile) {
        User user = userRepository.findByMobileNumber(mobile);
        return getAllIssuanceByUserId(user.getId());
    }

    public List<IssuanceOutDTO> getAllIssuanceByIssueTime(LocalDateTime issueTime) {
        List<Issuance> issuances = issuanceRepository.findAllByIssueTime(issueTime);
        return issuances.stream()
                .map(IssuanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<IssuanceOutDTO> getAllIssuanceByReturnTime(LocalDateTime returnTime) {
        List<Issuance> issuances = issuanceRepository.findAllByReturnTime(returnTime);
        return issuances.stream()
                .map(IssuanceMapper::toDTO)
                .collect(Collectors.toList());
    }
}

