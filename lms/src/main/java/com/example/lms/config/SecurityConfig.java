package com.example.lms.config;

import com.example.lms.exception.CustomAccessDeniedHandler;
import com.example.lms.exception.CustomBasicAuthenticationEntryPoint;
import com.example.lms.filter.JWTTokenValidatorFilter;
import com.example.lms.provider.CustomUsernamePwdAuthenticationProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
// import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {


        http
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrfConfig -> csrfConfig.disable())
                .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000/"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setMaxAge(3600L);

                        return config;
                    }
                }));

        http.authorizeHttpRequests((requests) -> requests
//                Other routes
                        .requestMatchers("/api/login", "/api/error", "/error").permitAll()
                        .requestMatchers("/api/user/register").permitAll()
                        .requestMatchers("/api/current-user").authenticated()


//                Category routes
                        .requestMatchers( "/api/categories", "/api/categories/**").hasRole("ADMIN")


//                Book routes
                        .requestMatchers("/api/books",  "/api/books/**").hasRole("ADMIN")


//                User routes
                        .requestMatchers( "/api/users", "/api/user/**").hasRole("ADMIN")


//                Issuance routes
                        .requestMatchers("/api/issuances/**").hasAnyRole("ADMIN", "USER")
//                Stats
                        .requestMatchers("/api/stats/**").hasRole("ADMIN")

        );

        http
                .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class);

        http.formLogin(formLoginConfig -> formLoginConfig.disable());

        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // by default it is using bcrypt password encoder
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        CustomUsernamePwdAuthenticationProvider authenticationProvider = new CustomUsernamePwdAuthenticationProvider(userDetailsService, passwordEncoder);
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

}

