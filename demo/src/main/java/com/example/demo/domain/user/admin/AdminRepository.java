package com.example.demo.domain.user.admin;

import org.springframework.stereotype.Repository;

import com.example.demo.doma.dao.UserAdminDao;
import com.example.demo.doma.entity.UserAdmin;

@Repository
public class AdminRepository {

    private final UserAdminDao userAdminDao;

    public AdminRepository(UserAdminDao userAdminDao) {
        this.userAdminDao = userAdminDao;
    }

    public void create(String userId, String userName, String email, String password) {
        
        UserAdmin userAdnmin = new UserAdmin();
        userAdnmin.setUserId(userId);
        userAdnmin.setUserName(userName);
        userAdnmin.setEmail(email);
        userAdnmin.setPasswordHash(password);

        userAdminDao.insert(userAdnmin);
    }
}
