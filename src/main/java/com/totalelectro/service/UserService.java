package com.totalelectro.service;

import com.totalelectro.model.User;
import com.totalelectro.dto.UserUpdateDTO;
import com.totalelectro.dto.UserDetailDTO;
import com.totalelectro.model.Order;
import java.util.List;

public interface UserService {
    User registerNewUser(User user);
    boolean existsByEmail(String email);
    void addRoleToUser(String email, String roleName);
    void removeRoleFromUser(String email, String roleName);
    User findByEmail(String email);
    User findByEmailOrNull(String email);
    void updateProfile(String email, User updatedUser);
    void changePassword(String email, String currentPassword, String newPassword, String confirmPassword);
    List<Order> getUserOrders(String email);
    
    // Métodos para administração
    List<User> getAllUsers();
    User findById(Long id);
    UserDetailDTO getUserDetailDTO(Long id);
    void deleteUser(Long id);
    void updateUser(User user);
    void updateUserFromDTO(Long userId, UserUpdateDTO dto);
    void toggleUserStatus(Long id);
} 