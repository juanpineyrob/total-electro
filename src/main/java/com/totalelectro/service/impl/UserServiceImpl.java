package com.totalelectro.service.impl;

import com.totalelectro.model.Role;
import com.totalelectro.model.User;
import com.totalelectro.model.Order;
import com.totalelectro.repository.RoleRepository;
import com.totalelectro.repository.UserRepository;
import com.totalelectro.repository.OrderRepository;
import com.totalelectro.service.UserService;
import com.totalelectro.dto.UserUpdateDTO;
import com.totalelectro.dto.UserDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

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
    public User findByEmailOrNull(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional
    public void updateProfile(String email, User updatedUser) {
        User existingUser = findByEmail(email);
        
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setCity(updatedUser.getCity());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        
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
        try {
            return orderRepository.findAllByUserEmailOrderByDateDesc(email);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    // Métodos para administração
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
    
    @Override
    public UserDetailDTO getUserDetailDTO(Long id) {
        User user = findById(id);
        return UserDetailDTO.fromUser(user);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        
        // Verificar se o usuário tem pedidos
        List<Order> userOrders = orderRepository.findAllByUserEmailOrderByDateDesc(user.getEmail());
        if (!userOrders.isEmpty()) {
            throw new RuntimeException("Não é possível excluir o usuário. Existem " + userOrders.size() + " pedido(s) associado(s) a este usuário.");
        }
        
        userRepository.delete(user);
    }
    
    @Override
    @Transactional
    public void updateUser(User user) {
        User existingUser = findById(user.getId());
        
        // Preservar a senha existente
        String currentPassword = existingUser.getPassword();
        
        // Atualizar dados básicos
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setCity(user.getCity());
        existingUser.setAddress(user.getAddress());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        
        // Restaurar a senha original
        existingUser.setPassword(currentPassword);
        
        // Atualizar roles se fornecidos
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            existingUser.setRoles(user.getRoles());
        }
        
        userRepository.save(existingUser);
    }
    
    @Override
    @Transactional
    public void updateUserFromDTO(Long userId, UserUpdateDTO dto) {
        User user = findById(userId);

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setCity(dto.getCity());
        user.setAddress(dto.getAddress());
        user.setPhoneNumber(dto.getPhoneNumber());

        if (dto.getRoles() != null) {
            java.util.Set<Role> newRoles = new java.util.HashSet<>();
            for (String roleName : dto.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role não encontrado: " + roleName));
                newRoles.add(role);
            }
            user.setRoles(newRoles);
        }
        
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long id) {
        // Para implementar ativação/desativação de usuários
        // Por enquanto, apenas um placeholder
        User user = findById(id);
        // Implementar lógica de toggle de status quando necessário
    }
} 