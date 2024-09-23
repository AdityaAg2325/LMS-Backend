package com.example.lms.services;

import com.example.lms.entity.User;
import com.example.lms.repository.UserRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRespository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Username: " + username);
        if (username.contains("@")) {
//            email
            User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User details not found for " + username));
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));

            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);

        } else {
//            mobileNumber
            User user = userRepository.findByMobileNumber(username).orElseThrow(() -> new UsernameNotFoundException("User details not found for " + username));
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));

            return new org.springframework.security.core.userdetails.User(user.getMobileNumber(), user.getPassword(), authorities);
        }
    }
}
