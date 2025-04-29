package com.example.demo.domain.user.admin;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.user.admin.entity.Admin;


@RestController
@RequestMapping("/api/user/admin")
public class AdminContoroller {

    private final AdminRepository adminRepository;
    private final AdminService adminService;

    AdminContoroller(AdminRepository adminRepository, AdminService adminService) {
        this.adminRepository = adminRepository;
        this.adminService = adminService;
    }
    
    @PostMapping("/create")
    public Admin postMethodName(@Validated @RequestBody Admin entity) {
        String encodedPassword = adminService.encodePassword(entity.getPassword());
        adminRepository.create(entity.getUserId(), entity.getUserName(), entity.getEmail(), encodedPassword);
        return entity;
    }
}
