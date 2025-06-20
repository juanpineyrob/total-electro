package com.totalelectro.controller;

import com.totalelectro.model.User;
import com.totalelectro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(Authentication authentication, HttpServletRequest request) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/profile";
        }
        
        // Guardar la URL de referencia para redirigir después del login
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.contains("/login")) {
            request.getSession().setAttribute("url_prior_login", referer);
        }
        
        return "login";
    }

    @GetMapping("/auth-status")
    @ResponseBody
    public Map<String, Object> getAuthStatus() {
        Map<String, Object> status = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        status.put("isAuthenticated", auth != null && auth.isAuthenticated());
        status.put("principal", auth != null ? auth.getPrincipal() : null);
        status.put("authorities", auth != null ? auth.getAuthorities() : null);
        
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            status.put("username", userDetails.getUsername());
            status.put("roles", userDetails.getAuthorities());
        }
        
        return status;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.registerNewUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Registro exitoso. Por favor inicia sesión.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }
} 