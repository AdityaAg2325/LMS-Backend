package com.example.lms.controller;

import com.example.lms.dto.CategoryOutDTO;
import com.example.lms.dto.UserOutDTO;
import com.example.lms.dto.UserRegisterDTO;
import com.example.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserOutDTO>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @GetMapping("/paginatedUsers")
    public ResponseEntity<Page<UserOutDTO>> getPaginatedUsers(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize,  @RequestParam String search){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsersPaginated(pageNumber, pageSize, search));
    }

    @GetMapping("/{mobileNumber}")
    public ResponseEntity<UserOutDTO> getUserByMobile(@PathVariable String mobileNumber){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByMobile(mobileNumber));
    }

    @DeleteMapping("/{mobileNumber}")
    public ResponseEntity<UserOutDTO> deleteUserByMobile(@PathVariable String mobileNumber){
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUserByMobile(mobileNumber));
    }

    @GetMapping("/userCount")
    public ResponseEntity<Long> countAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.countAllUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<UserOutDTO> createUser(@RequestBody UserRegisterDTO userRegisterDTO){
        return ResponseEntity.status(HttpStatus.OK).body(userService.createUser(userRegisterDTO));
    }

    @PutMapping("/{mobileNumber}")
    public ResponseEntity<UserOutDTO> updateUserByMobile(@PathVariable String mobileNumber, @RequestBody UserRegisterDTO userRegisterDTO){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserByMobile(mobileNumber, userRegisterDTO));
    }
}
