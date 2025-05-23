package com.totalelectro.service;

import com.totalelectro.model.User;
import com.totalelectro.model.Order;
import java.util.List;

public interface UserService {
    User registerNewUser(User user);
    boolean existsByEmail(String email);
    void addRoleToUser(String email, String roleName);
    void removeRoleFromUser(String email, String roleName);
    User findByEmail(String email);
    void updateProfile(String email, User updatedUser);
    void changePassword(String email, String currentPassword, String newPassword, String confirmPassword);
    List<Order> getUserOrders(String email);
} 