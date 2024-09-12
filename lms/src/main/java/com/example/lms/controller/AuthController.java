package com.example.lms.controller;

import com.example.lms.constants.JWTConstants;
import com.example.lms.dto.UserLoginDTO;
import com.example.lms.dto.UserOutDTO;
import com.example.lms.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final Environment env;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserOutDTO> login(@RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) {

        String encodedPassword = userLoginDTO.getPassword();
        byte[] decodedBytes = Base64.getDecoder().decode(encodedPassword);
        String decodedPassword = new String(decodedBytes);
        userLoginDTO.setPassword(decodedPassword);

        String jwt = "";
        System.out.println("Controller: username = " + userLoginDTO.getUserName() + ", password = " + userLoginDTO.getPassword());
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(userLoginDTO.getUserName(),
                userLoginDTO.getPassword());

        Authentication authenticationResponse = authenticationManager.authenticate(authentication);

        if(null != authenticationResponse && authenticationResponse.isAuthenticated()) {
            if (null != env) {
                String secret = env.getProperty(JWTConstants.JWT_SECRET_KEY,
                        JWTConstants.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                jwt = Jwts.builder().issuer("LMS").subject("JWT Token")
                        .claim("username", authenticationResponse.getName())
                        .claim("authorities", authenticationResponse.getAuthorities().stream().map(
                                GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                        .issuedAt(new java.util.Date())
                        .expiration(new java.util.Date((new java.util.Date()).getTime() + 30000000))
                        .signWith(secretKey).compact();
            }
        }

        List<GrantedAuthority> grantedAuthorities = (List<GrantedAuthority>) authenticationResponse.getAuthorities();
        List<String> roles = new ArrayList<>();
        grantedAuthorities.forEach(grantedAuthority -> roles.add(grantedAuthority.toString()));

        String username = userLoginDTO.getUserName();

        UserOutDTO userDTO = new UserOutDTO();

        if (username.contains("@")) {
//            email
            userDTO = userService.getUserByEmail(username);
        } else {
//            mobileNumber
            userDTO = userService.getUserByMobile(username);
        }

        userDTO.setToken(jwt);
        userDTO.setRole(roles.get(0));

        return ResponseEntity.status(HttpStatus.OK).header(JWTConstants.JWT_HEADER,jwt)
                .body(userDTO);
    }


    @GetMapping("/current-user")
    public ResponseEntity<UserOutDTO> currentUser(@RequestHeader("Authorization") String token) {
        UserOutDTO user = userService.getUserByToken(token);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}