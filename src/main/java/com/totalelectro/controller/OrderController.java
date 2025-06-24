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
import org.springframework.http.ResponseEntity;
import java.util.stream.Collectors;
import java.util.List;

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

    @GetMapping("/{id}/json")
    public ResponseEntity<?> getOrderJson(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Order order = orderService.findByIdWithProducts(id);
        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = order.getUser().getEmail().equals(userDetails.getUsername());
        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(403).body("Acesso negado");
        }
        return ResponseEntity.ok(new OrderDetailsDTO(order));
    }
}

// DTO para resposta JSON
class OrderDetailsDTO {
    public Long id;
    public String date;
    public String status;
    public Double totalPrice;
    public String firstName;
    public String lastName;
    public String email;
    public String address;
    public String city;
    public String phoneNumber;
    public List<ProductDTO> products;
    static class ProductDTO {
        public Long id;
        public String name;
        public ProductDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    public OrderDetailsDTO(com.totalelectro.model.Order order) {
        this.id = order.getId();
        this.date = order.getDate().toString();
        this.status = order.getStatus().name();
        this.totalPrice = order.getTotalPrice();
        this.firstName = order.getFirstName();
        this.lastName = order.getLastName();
        this.email = order.getEmail();
        this.address = order.getAddress();
        this.city = order.getCity();
        this.phoneNumber = order.getPhoneNumber();
        this.products = order.getProducts().stream()
            .map(p -> new ProductDTO(p.getId(), p.getName()))
            .collect(Collectors.toList());
    }
} 