package com.totalelectro.controller;

import com.totalelectro.model.Order;
import com.totalelectro.model.OrderStatus;
import com.totalelectro.model.Product;
import com.totalelectro.model.User;
import com.totalelectro.service.OrderService;
import com.totalelectro.service.ProductService;
import com.totalelectro.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping
    public String showCheckout(@RequestParam(required = false) Long productId,
                              @RequestParam(defaultValue = "1") int quantity,
                              Model model) {
        
        // Verificar se o usuário está autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        
        User user = null;
        if (isAuthenticated) {
            user = userService.findByEmail(auth.getName());
        }
        
        // Se tem um produto específico, buscar os dados
        Product product = null;
        if (productId != null) {
            product = productService.getProductById(productId);
        }
        
        // Calcular valores
        double subtotal = product != null ? product.getPrice().doubleValue() * quantity : 0;
        double shipping = 50.0;
        double tax = subtotal * 0.21; // 21% IVA
        double total = subtotal + shipping + tax;
        
        model.addAttribute("user", user);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("product", product);
        model.addAttribute("quantity", quantity);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shipping", shipping);
        model.addAttribute("tax", tax);
        model.addAttribute("total", total);
        
        return "checkout";
    }
    
    @PostMapping("/process")
    public String processCheckout(@RequestParam Long productId,
                                 @RequestParam int quantity,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String email,
                                 @RequestParam String phone,
                                 @RequestParam String address,
                                 @RequestParam String city,
                                 @RequestParam String state,
                                 @RequestParam String zipCode,
                                 RedirectAttributes redirectAttributes) {
        
        try {
            // Buscar o produto
            Product product = productService.getProductById(productId);
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Produto não encontrado");
                return "redirect:/";
            }
            
            // Calcular valores
            double subtotal = product.getPrice().doubleValue() * quantity;
            double shipping = 50.0;
            double tax = subtotal * 0.21; // 21% IVA
            double total = subtotal + shipping + tax;
            
            // Criar o pedido
            Order order = new Order();
            order.setTotalPrice(total);
            order.setDate(LocalDateTime.now());
            order.setStatus(OrderStatus.PENDIENTE);
            order.setFirstName(firstName);
            order.setLastName(lastName);
            order.setEmail(email);
            order.setPhoneNumber(phone);
            order.setAddress(address);
            order.setCity(city);
            
            // Adicionar o produto ao pedido
            Set<Product> products = new HashSet<>();
            products.add(product);
            order.setProducts(products);
            
            // Verificar se o usuário está logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
            
            if (isAuthenticated) {
                // Usuário logado - usar o serviço que associa automaticamente
                Order savedOrder = orderService.save(order, auth.getName());
                redirectAttributes.addFlashAttribute("success", "Pedido realizado com sucesso! Número do pedido: #" + savedOrder.getId());
            } else {
                // Usuário não logado - buscar usuário existente ou criar um temporário
                User user = userService.findByEmailOrNull(email);
                if (user == null) {
                    // Usuário não existe, vamos criar um temporário
                    user = new User();
                    user.setEmail(email);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setCity(city);
                    user.setAddress(address);
                    user.setPhoneNumber(phone);
                    user.setPassword("$2a$10$CD0AKAgdinolsfw7mz1QBu4egK8Tcjm.N0eAPB1.VbHXjn2grs8mm"); // senha temporária
                    user = userService.registerNewUser(user);
                }
                
                order.setUser(user);
                Order savedOrder = orderService.save(order, user.getEmail());
                redirectAttributes.addFlashAttribute("success", "Pedido realizado com sucesso! Número do pedido: #" + savedOrder.getId());
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao processar pedido: " + e.getMessage());
        }
        
        return "redirect:/";
    }
} 