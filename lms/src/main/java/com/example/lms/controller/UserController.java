package com.example.lms.controller;

import com.example.lms.constants.UserConstants;
import com.example.lms.dto.CategoryOutDTO;
import com.example.lms.dto.ResponseDTO;
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
    public ResponseEntity<Page<UserOutDTO>> getPaginatedUsers(@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "id") String sortBy,
                                                              @RequestParam(defaultValue = "desc") String sortDir,  @RequestParam String search){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsersPaginated(pageNumber, pageSize, sortBy, sortDir, search));
    }

    @GetMapping("/{mobileNumber}")
    public ResponseEntity<UserOutDTO> getUserByMobile(@PathVariable String mobileNumber){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByMobile(mobileNumber));
    }

    @GetMapping("/userCount")
    public ResponseEntity<Long> countAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.countAllUsers());
    }

    @DeleteMapping("/{mobileNumber}")
    public ResponseEntity<ResponseDTO> deleteUserByMobile(@PathVariable String mobileNumber){
        userService.deleteUserByMobile(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(HttpStatus.OK.toString(), UserConstants.USER_DELETE_MSG));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> createUser(@RequestBody UserRegisterDTO userRegisterDTO){
        userService.createUser(userRegisterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(HttpStatus.CREATED.toString(), UserConstants.USER_CREATE_MSG));
    }

    @PutMapping("/{mobileNumber}")
    public ResponseEntity<ResponseDTO> updateUserByMobile(@PathVariable String mobileNumber, @RequestBody UserRegisterDTO userRegisterDTO){
        userService.updateUserByMobile(mobileNumber, userRegisterDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(HttpStatus.OK.toString(), UserConstants.USER_UPDATE_MSG));
    }
}
