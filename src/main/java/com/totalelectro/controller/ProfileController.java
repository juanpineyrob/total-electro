package com.totalelectro.controller;

import com.totalelectro.model.User;
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

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "profile/index";
    }

    @GetMapping("/edit")
    public String showEditForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "profile/edit";
    }

    @PostMapping("/update")
    public String updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute User user,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "profile/edit";
        }

        try {
            userService.updateProfile(userDetails.getUsername(), user);
            redirectAttributes.addFlashAttribute("successMessage", "Perfil actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "profile/change-password";
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
            redirectAttributes.addFlashAttribute("successMessage", "Contraseña actualizada exitosamente");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile/change-password";
        }
    }

    @GetMapping("/orders")
    public String showOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("orders", userService.getUserOrders(userDetails.getUsername()));
        return "profile/orders";
    }
} 