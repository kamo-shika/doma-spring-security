package com.example.demo.domain.auth;

import java.util.Collections;
import java.util.Objects;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.doma.entity.UserAdmin;
import com.example.demo.domain.user.admin.AdminRepository;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;

    public UserDetailServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserAdmin user = adminRepository.findByUserId(username);

        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("User not found");
        } else {
            return new User(user.getUserId(), user.getPasswordHash(), 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }
    }
    
}
