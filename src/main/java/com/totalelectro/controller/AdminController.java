package com.totalelectro.controller;

import com.totalelectro.service.OrderService;
import com.totalelectro.service.ProductService;
import com.totalelectro.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        Double currentMonthSales = orderService.getCurrentMonthSales();
        
        // Adicionar dados ao model
        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalProducts", totalProducts);
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
} 