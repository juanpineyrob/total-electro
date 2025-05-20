package com.totalelectro.service;

import com.totalelectro.model.User;

public interface UserService {
    User registerNewUser(User user);
    boolean existsByEmail(String email);
    void addRoleToUser(String email, String roleName);
    void removeRoleFromUser(String email, String roleName);
} 