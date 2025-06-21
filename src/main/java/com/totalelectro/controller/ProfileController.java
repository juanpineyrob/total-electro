package com.totalelectro.controller;

import com.totalelectro.dto.UserProfileDTO;
import com.totalelectro.model.User;
import com.totalelectro.model.Order;
import com.totalelectro.model.Role;
import com.totalelectro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        
        // Criar DTO com os dados do usuário
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFirstName(user.getFirstName());
        userProfileDTO.setLastName(user.getLastName());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setCity(user.getCity());
        userProfileDTO.setAddress(user.getAddress());
        userProfileDTO.setPhoneNumber(user.getPhoneNumber());
        
        // Buscar pedidos do usuário
        List<Order> orders = userService.getUserOrders(userDetails.getUsername());
        
        model.addAttribute("user", user);
        model.addAttribute("userProfileDTO", userProfileDTO);
        model.addAttribute("orders", orders);
        
        return "profile/index";
    }

    @PostMapping("/update")
    public String updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute UserProfileDTO userProfileDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar o perfil. Verifique os dados.");
            return "redirect:/profile#info";
        }

        try {
            // Converter DTO para User
            User userToUpdate = new User();
            userToUpdate.setFirstName(userProfileDTO.getFirstName());
            userToUpdate.setLastName(userProfileDTO.getLastName());
            userToUpdate.setEmail(userProfileDTO.getEmail());
            userToUpdate.setCity(userProfileDTO.getCity());
            userToUpdate.setAddress(userProfileDTO.getAddress());
            userToUpdate.setPhoneNumber(userProfileDTO.getPhoneNumber());
            
            userService.updateProfile(userDetails.getUsername(), userToUpdate);
            redirectAttributes.addFlashAttribute("successMessage", "Perfil atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro: " + e.getMessage());
        }

        return "redirect:/profile#info";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        try {
            userService.changePassword(
                userDetails.getUsername(),
                currentPassword,
                newPassword,
                confirmPassword
            );
            redirectAttributes.addFlashAttribute("successMessage", "Senha atualizada com sucesso!");
            return "redirect:/profile#password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile#password";
        }
    }
} 