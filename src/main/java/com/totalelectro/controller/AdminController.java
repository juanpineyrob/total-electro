package com.totalelectro.controller;

import com.totalelectro.model.User;
import com.totalelectro.service.OrderService;
import com.totalelectro.service.ProductService;
import com.totalelectro.service.UserService;
import com.totalelectro.dto.UserUpdateDTO;
import com.totalelectro.dto.UserDetailDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;
    private final ProductService productService;
    private final UserService userService;

    @GetMapping
    public String adminDashboard(Model model) {
        // Buscar todos os pedidos com produtos
        var orders = orderService.findAllWithProducts();
        
        // Estatísticas básicas
        long totalOrders = orders.size();
        long totalProducts = productService.getAllProducts().size();
        long totalUsers = userService.getAllUsers().size();
        Double currentMonthSales = orderService.getCurrentMonthSales();
        
        // Adicionar dados ao model
        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("currentMonthSales", currentMonthSales);
        
        return "admin/dashboard";
    }

    @GetMapping("/orders")
    public String adminOrders(Model model) {
        var orders = orderService.findAllWithProducts();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String adminOrderDetail(@PathVariable Long id, Model model) {
        var order = orderService.findByIdWithProducts(id);
        model.addAttribute("order", order);
        return "admin/order-detail";
    }

    @PostMapping("/orders/{id}/complete")
    public String completeOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.completeOrder(id);
            redirectAttributes.addFlashAttribute("success", "Pedido completado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al completar el pedido: " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("success", "Pedido cancelado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar el pedido: " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }
    
    // Métodos para gerenciar usuários
    @GetMapping("/users")
    public String adminUsers(Model model) {
        var users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    @GetMapping("/users/{id}")
    public String adminUserDetail(@PathVariable Long id, Model model) {
        var user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/user-detail";
    }
    
    @GetMapping("/users/{id}/json")
    @ResponseBody
    public ResponseEntity<UserDetailDTO> getUserJson(@PathVariable Long id) {
        try {
            UserDetailDTO user = userService.getUserDetailDTO(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/users/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Usuário excluído com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erro ao excluir usuário: " + e.getMessage()));
        }
    }
    
    @PostMapping("/users/{id}/update")
    @ResponseBody
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            userService.updateUserFromDTO(id, userUpdateDTO);
            return ResponseEntity.ok(Map.of("message", "Usuário atualizado com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 