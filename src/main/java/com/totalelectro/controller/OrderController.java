package com.totalelectro.controller;

import com.totalelectro.model.Order;
import com.totalelectro.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listAllOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders/list";
    }

    @GetMapping("/my-orders")
    @PreAuthorize("isAuthenticated()")
    public String listUserOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("orders", orderService.findByUserEmail(userDetails.getUsername()));
        return "orders/my-orders";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.isOrderOwner(#id, authentication.principal.username)")
    public String viewOrder(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id);
        if (order != null) {
            model.addAttribute("order", order);
            return "orders/view";
        }
        return "redirect:/orders";
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String createOrder(@ModelAttribute Order order, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            orderService.save(order, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("success", "Pedido creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el pedido: " + e.getMessage());
        }
        return "redirect:/orders/my-orders";
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @orderService.isOrderOwner(#id, authentication.principal.username)")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("success", "Pedido cancelado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar el pedido: " + e.getMessage());
        }
        return "redirect:/orders/my-orders";
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public String completeOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.completeOrder(id);
            redirectAttributes.addFlashAttribute("success", "Pedido completado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al completar el pedido: " + e.getMessage());
        }
        return "redirect:/orders";
    }
} 