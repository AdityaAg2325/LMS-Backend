package com.example.lms.services;

import com.example.lms.constants.IssuanceConstants;
import com.example.lms.dto.*;
import com.example.lms.entity.Book;
import com.example.lms.entity.Category;
import com.example.lms.entity.Issuance;
import com.example.lms.entity.User;
import com.example.lms.exception.ResourceNotFoundException;
import com.example.lms.mapper.BookMapper;
import com.example.lms.mapper.IssuanceMapper;
import com.example.lms.repository.BooksRepository;
import com.example.lms.repository.IssuanceRepository;
import com.example.lms.repository.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private SMSService smsService;

//    public IssuanceOutDTO createIssuance(IssuanceInDTO inDTO) {
//        User user = userRepository.findById(inDTO.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        Book book = bookRepository.findById(inDTO.getBookId())
//                .orElseThrow(() -> new RuntimeException("Book not found"));
//
//        Issuance issuance = IssuanceMapper.toEntity(inDTO, user, book);
//        issuance.setIssueTime(LocalDateTime.now());
//        issuance.setExpectedReturnTime(inDTO.getReturnTime());
//        issuance = issuanceRepository.save(issuance);
//
//        String message = String.format("Issued book: '%s'" + " Author name: '%s'",
//                issuance.getBook().getTitle(),
//                issuance.getBook().getAuthor());
//
//        return IssuanceMapper.toDTO(issuance);
//    }

    public Page<IssuanceOutDTO> getIssuances(Pageable pageable, String search) {
        Page<Issuance> issuancePage;
        if (search != null && !search.isEmpty()) {
            issuancePage = issuanceRepository.findByBookContainingIgnoreCase(search, pageable);
            if (issuancePage.isEmpty()) {
                issuancePage = issuanceRepository.findByUserNameContainingIgnoreCase(search, pageable);
            }
        } else {
            issuancePage = issuanceRepository.findAll(pageable);
        }

        return issuancePage.map(issuance -> IssuanceMapper.toDTO(issuance));
    }

    public Page<UserHistoryDTO> getUserHistory(Pageable pageable, String mobile) {
        User user = userRepository.findByMobileNumber(mobile).orElseThrow(
                () -> new ResourceNotFoundException("User", "mobileNumber", mobile)
        );

        Page<Issuance> issuancePage = issuanceRepository.findByUserId(
                user.getId(), pageable
        );

        List<UserHistoryDTO> userHistory = issuancePage.stream().map(issuance -> {
            UserHistoryDTO dto = new UserHistoryDTO();
            dto.setId(issuance.getId());
            dto.setBook(BookMapper.toDTO(issuance.getBook()));
            dto.setStatus(issuance.getStatus());
            dto.setType(issuance.getType());
            dto.setIssueTime(issuance.getIssueTime());
            dto.setExpectedReturnTime(issuance.getExpectedReturnTime());
            dto.setActualReturnTime(issuance.getActualReturnTime());
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(userHistory, pageable, issuancePage.getTotalElements());
    }

    public Page<BookHistoryDTO> getBookHistory(Pageable pageable, Long id) {
        List<Issuance> issuanceList = issuanceRepository.findAllByBookId(id);
        List<BookHistoryDTO> bookHistory = issuanceList.stream().map(issuance -> {
            BookHistoryDTO dto = new BookHistoryDTO();
            dto.setId(issuance.getId());
            dto.setUser(issuance.getUser());
            dto.setStatus(issuance.getStatus());
            dto.setType(issuance.getType());
            dto.setIssueTime(issuance.getIssueTime());
            dto.setExpectedReturnTime(issuance.getExpectedReturnTime());
            dto.setActualReturnTime(issuance.getActualReturnTime());
            return dto;
        }).collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), bookHistory.size());
        List<BookHistoryDTO> pagedHistory = bookHistory.subList(start, end);

        return new PageImpl<>(pagedHistory, pageable, bookHistory.size());
    }


    public IssuanceOutDTO createIssuance(IssuanceInDTO inDTO) {
        User user = userRepository.findById(inDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", inDTO.getUserId().toString()));
        Book book = bookRepository.findById(inDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", inDTO.getBookId().toString()));

        Issuance issuance = IssuanceMapper.toEntity(inDTO, user, book);
        issuance.setIssueTime(LocalDateTime.now());
        issuance.setExpectedReturnTime(inDTO.getReturnTime());

        if (book.getCurrQty() > 0) {
            book.setCurrQty(book.getCurrQty()-1);
            bookRepository.save(book);
        } else {
            throw new IllegalStateException("The book quantity is 0 and cannot be issued!");
        }


        Issuance savedIssuance = issuanceRepository.save(issuance);

        String message = String.format("\nYou have successfully issued the book '%s'\n" +
                        "From %s\n" +
                        "To %s",
                savedIssuance.getBook().getTitle(),
                savedIssuance.getIssueTime().toLocalDate(),
                savedIssuance.getExpectedReturnTime().toLocalDate());

        if (savedIssuance.getType().equals(IssuanceConstants.ISSUANCE_TYPE_TAKE_AWAY)) {
            smsService.sendSms(savedIssuance.getUser().getMobileNumber(), message);
        }

        IssuanceOutDTO issuanceOutDTO = IssuanceMapper.toDTO(savedIssuance);

        return issuanceOutDTO;
    }



    public IssuanceOutDTO getIssuanceById(Long id) {
        Issuance issuance = issuanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issuance", "id", id.toString()));
        return IssuanceMapper.toDTO(issuance);
    }

    public Long getTotalActiveUsers() {
        return issuanceRepository.countDistinctUsersByStatus("ISSUED");
    }

    public IssuanceOutDTO deleteIssuanceById(Long id) {
        Issuance issuance = issuanceRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Issuance", "id", id.toString())
        );

        issuanceRepository.deleteById(id);

        IssuanceOutDTO issuanceOutDTO = IssuanceMapper.toDTO(issuance);

        return  issuanceOutDTO;
    }

    public IssuanceOutDTO updateIssuance(Long id, IssuanceInDTO inDTO) {
        Issuance issuance = issuanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issuance", "id", id.toString()));

        String oldSattus = issuance.getStatus();
        LocalDateTime oldIssueTime = issuance.getIssueTime();
        LocalDateTime oldExpReturnTime = issuance.getExpectedReturnTime();
        LocalDateTime oldActualReturnTime = issuance.getActualReturnTime();
        String oldIssuanceType = issuance.getType();

        issuance = IssuanceMapper.toEntity(inDTO, issuance.getUser(), issuance.getBook());
        issuance.setActualReturnTime(oldActualReturnTime);

        if (issuance.getExpectedReturnTime() == null) {
            issuance.setExpectedReturnTime(oldExpReturnTime);
        }

        issuance.setIssueTime(oldIssueTime);

        issuance.setId(id);
        Book book = issuance.getBook();
        if (inDTO.getStatus().equals(IssuanceConstants.ISSUANCE_STATUS_RETURNED) && oldSattus.equals(IssuanceConstants.ISSUANCE_STATUS_ISSUED)) {
            issuance.setActualReturnTime(LocalDateTime.now());
            if (book.getCurrQty() < book.getQuantity()) {
                book.setCurrQty(book.getCurrQty() + 1);
                bookRepository.save(book);
            }

        } else if (inDTO.getStatus().equals(IssuanceConstants.ISSUANCE_STATUS_RETURNED) && oldSattus.equals(IssuanceConstants.ISSUANCE_STATUS_RETURNED)) {

        } else if (inDTO.getStatus().equals(IssuanceConstants.ISSUANCE_STATUS_ISSUED) && oldSattus.equals(IssuanceConstants.ISSUANCE_STATUS_RETURNED)) {
            if (book.getCurrQty() > 0) {
                book.setCurrQty(book.getCurrQty() - 1);
                bookRepository.save(book);
            }
            issuance.setActualReturnTime(null);
        } else {
            issuance.setActualReturnTime(null);
        }

        System.out.println(issuance);
        Issuance savedIssuance = issuanceRepository.save(issuance);

        IssuanceOutDTO issuanceOutDTO = IssuanceMapper.toDTO(savedIssuance);

        return issuanceOutDTO;
    }



}

