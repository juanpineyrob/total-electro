package com.totalelectro.service.impl;

import com.totalelectro.model.Role;
import com.totalelectro.model.User;
import com.totalelectro.model.Order;
import com.totalelectro.repository.RoleRepository;
import com.totalelectro.repository.UserRepository;
import com.totalelectro.repository.OrderRepository;
import com.totalelectro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerNewUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Encriptar la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Asignar rol USER por defecto
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));
        user.addRole(userRole);
        
        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void addRoleToUser(String email, String roleName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
        
        user.addRole(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeRoleFromUser(String email, String roleName) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        
        user.removeRole(role);
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    @Transactional
    public void updateProfile(String email, User updatedUser) {
        User existingUser = findByEmail(email);
        
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        
        // Si el email es diferente, verificar que no exista
        if (!email.equals(updatedUser.getEmail()) && userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new RuntimeException("El nuevo email ya está registrado");
        }
        existingUser.setEmail(updatedUser.getEmail());
        
        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword, String confirmPassword) {
        User user = findByEmail(email);
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<Order> getUserOrders(String email) {
        return orderRepository.findAllByUserEmailOrderByDateDesc(email);
    }
} 