package com.example.lms.controller;

import com.example.lms.dto.IssuanceInDTO;
import com.example.lms.dto.IssuanceOutDTO;
import com.example.lms.services.IssuanceService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/create")
    public ResponseEntity<IssuanceOutDTO> createIssuance(@RequestBody IssuanceInDTO issuanceInDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issuanceService.createIssuance(issuanceInDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssuanceOutDTO> getIssuanceById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(issuanceService.getIssuanceById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIssuanceById(@PathVariable Long id) {
        issuanceService.deleteIssuanceById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Issuance deleted successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssuanceOutDTO> updateIssuance(@PathVariable Long id, @RequestBody IssuanceInDTO issuanceInDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(issuanceService.updateIssuance(id, issuanceInDTO));
    }

    @PutMapping("/updateStatus/{id}")
    public ResponseEntity<IssuanceOutDTO> updateStatus(@PathVariable Long id, @RequestBody IssuanceInDTO issuanceInDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(issuanceService.updateStatus(id, issuanceInDTO));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IssuanceOutDTO>> getAllIssuancesByUserId(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(issuanceService.getAllIssuanceByUserId(userId));
    }

    @GetMapping("/mobile/{mobileNumber}")
    public ResponseEntity<List<IssuanceOutDTO>> getAllIssuancesByMobile(@PathVariable String mobileNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(issuanceService.getAllIssuanceByMobile(mobileNumber));
    }

    @GetMapping("/{issueTime}")
    public ResponseEntity<List<IssuanceOutDTO>> getAllIssuancesByIssueTime(@PathVariable LocalDateTime issueTime) {
        return ResponseEntity.status(HttpStatus.OK).body(issuanceService.getAllIssuanceByIssueTime(issueTime));
    }

    @GetMapping("/{returnTime}")
    public ResponseEntity<List<IssuanceOutDTO>> getAllIssuancesByReturnTime(@PathVariable LocalDateTime returnTime) {
        return ResponseEntity.status(HttpStatus.OK).body(issuanceService.getAllIssuanceByReturnTime(returnTime));
    }

}

