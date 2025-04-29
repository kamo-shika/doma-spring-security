package com.example.demo.domain.user.admin;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    
    private final PasswordEncoder passwordEncoder;
    AdminService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

}
