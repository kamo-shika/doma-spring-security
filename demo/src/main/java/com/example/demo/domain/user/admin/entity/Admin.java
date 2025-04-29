package com.example.demo.domain.user.admin.entity;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Admin {

    @NotEmpty
    String userId;

    @NotEmpty
    String userName;

    @NotEmpty
    String email;

    @NotEmpty
    String password;
}
