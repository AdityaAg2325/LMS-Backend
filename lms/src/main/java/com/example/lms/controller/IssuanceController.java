package com.example.lms.controller;

import com.example.lms.constants.IssuanceConstants;
import com.example.lms.dto.*;
import com.example.lms.services.IssuanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/issuances")
public class IssuanceController {

    @Autowired
    private IssuanceService issuanceService;


    @GetMapping
    public ResponseEntity<Page<IssuanceOutDTO>> getAllIssuances(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search
    ){
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(sortDir), sortBy);
        Page<IssuanceOutDTO> issuanceOutDTOPage = issuanceService.getIssuances(pageable, search);
        return ResponseEntity.status(HttpStatus.OK).body(issuanceOutDTOPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssuanceOutDTO> getIssuanceById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(issuanceService.getIssuanceById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> createIssuance(@RequestBody IssuanceInDTO issuanceInDTO) {
        issuanceService.createIssuance(issuanceInDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(HttpStatus.OK.toString(), IssuanceConstants.ISSUANCE_CREATE_MSG));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deleteIssuanceById(@PathVariable Long id) {
        issuanceService.deleteIssuanceById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(HttpStatus.OK.toString(), IssuanceConstants.ISSUANCE_DELETE_MSG));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateIssuance(@PathVariable Long id, @RequestBody IssuanceInDTO issuanceInDTO) {
        issuanceService.updateIssuance(id, issuanceInDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(HttpStatus.OK.toString(), IssuanceConstants.ISSUANCE_UPDATE_MSG));
    }


    @GetMapping("/pageable")
    public ResponseEntity<Page<IssuanceOutDTO>> getAllIssuancesPageable (
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search
    ) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(sortDir), sortBy);
        Page<IssuanceOutDTO> issuanceOutDTOPage = issuanceService.getIssuances(pageable, search);

        return ResponseEntity.status(HttpStatus.OK).body(issuanceOutDTOPage);

    }

@GetMapping("/user/history/{mobile}")
public ResponseEntity<Page<UserHistoryDTO>> getUserHistory(
        @PathVariable String mobile,
        @RequestParam(defaultValue = "0") Integer pageNumber, // Provide default value
        @RequestParam(defaultValue = "10") Integer pageSize,  // Provide default value
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir) {

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(sortDir), sortBy);

    // Fetch the user history
    Page<UserHistoryDTO> userHistoryDTOPage = issuanceService.getUserHistory(pageable, mobile);

    return ResponseEntity.status(HttpStatus.OK).body(userHistoryDTOPage);
}


    @GetMapping("/book/history/{id}")
    public ResponseEntity<Page<BookHistoryDTO>> getBookHistory(@PathVariable Long id,
                                                               @RequestParam(defaultValue = "0") Integer pageNumber,
                                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                                               @RequestParam(defaultValue = "id") String sortBy,
                                                               @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(sortDir), sortBy);
        Page<BookHistoryDTO> bookHistoryDTOPage = issuanceService.getBookHistory(pageable, id);

        return ResponseEntity.status(HttpStatus.OK).body(bookHistoryDTOPage);
    }

}



