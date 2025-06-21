package com.totalelectro.controller;

import com.totalelectro.model.Order;
import com.totalelectro.model.OrderStatus;
import com.totalelectro.model.Product;
import com.totalelectro.model.User;
import com.totalelectro.model.CartItem;
import com.totalelectro.service.OrderService;
import com.totalelectro.service.ProductService;
import com.totalelectro.service.UserService;
import com.totalelectro.service.CartService;
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
import java.util.List;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final CartService cartService;

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

    @GetMapping("/cart")
    public String showCartCheckout(Model model) {
        
        // Verificar se o usuário está autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        
        if (!isAuthenticated) {
            return "redirect:/login";
        }
        
        String userEmail = auth.getName();
        User user = userService.findByEmail(userEmail);
        List<CartItem> cartItems = cartService.getCartItems(userEmail);
        
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        
        // Calcular valores
        double subtotal = cartService.getCartTotal(userEmail);
        double shipping = 50.0;
        double tax = subtotal * 0.21; // 21% IVA
        double total = subtotal + shipping + tax;
        
        model.addAttribute("user", user);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shipping", shipping);
        model.addAttribute("tax", tax);
        model.addAttribute("total", total);
        model.addAttribute("isCartCheckout", true);
        
        return "checkout";
    }
    
    @PostMapping("/process")
    public String processCheckout(@RequestParam(required = false) Long productId,
                                 @RequestParam(required = false) Integer quantity,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String email,
                                 @RequestParam String phone,
                                 @RequestParam String address,
                                 @RequestParam String city,
                                 @RequestParam String state,
                                 @RequestParam String zipCode,
                                 RedirectAttributes redirectAttributes) {
        // Validación de email
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            redirectAttributes.addFlashAttribute("error", "E-mail inválido");
            return "redirect:/checkout";
        }
        // Validación de CEP
        if (zipCode == null || !zipCode.matches("^\\d{5}-?\\d{3}$")) {
            redirectAttributes.addFlashAttribute("error", "CEP inválido");
            return "redirect:/checkout";
        }
        
        try {
            // Buscar el producto
            Product product = null;
            if (productId != null) {
                product = productService.getProductById(productId);
                if (product == null) {
                    redirectAttributes.addFlashAttribute("error", "Produto não encontrado");
                    return "redirect:/";
                }
            }
            
            // Calcular valores
            double subtotal = product != null && quantity != null ? product.getPrice().doubleValue() * quantity : 0;
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
            if (product != null) {
                products.add(product);
            }
            order.setProducts(products);
            
            // Verificar se o usuário está logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
            
            Order savedOrder;
            if (isAuthenticated) {
                // Usuário logado - usar o serviço que associa automaticamente
                savedOrder = orderService.save(order, auth.getName());
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
                savedOrder = orderService.save(order, user.getEmail());
            }
            
            // Redirigir a la página de confirmación
            redirectAttributes.addFlashAttribute("order", savedOrder);
            return "redirect:/checkout/confirmation";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao processar pedido: " + e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/process-cart")
    public String processCartCheckout(@RequestParam String firstName,
                                     @RequestParam String lastName,
                                     @RequestParam String email,
                                     @RequestParam String phone,
                                     @RequestParam String address,
                                     @RequestParam String city,
                                     @RequestParam String state,
                                     @RequestParam String zipCode,
                                     RedirectAttributes redirectAttributes) {
        
        try {
            // Verificar se o usuário está logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
            
            if (!isAuthenticated) {
                redirectAttributes.addFlashAttribute("error", "Usuário não autenticado");
                return "redirect:/login";
            }
            
            String userEmail = auth.getName();
            List<CartItem> cartItems = cartService.getCartItems(userEmail);
            
            if (cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Carrinho vazio");
                return "redirect:/cart";
            }
            
            // Calcular valores
            double subtotal = cartService.getCartTotal(userEmail);
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
            
            // Adicionar produtos do carrinho ao pedido
            Set<Product> products = new HashSet<>();
            for (CartItem cartItem : cartItems) {
                products.add(cartItem.getProduct());
            }
            order.setProducts(products);
            
            // Salvar o pedido
            Order savedOrder = orderService.save(order, userEmail);
            
            // Limpar o carrinho após o checkout
            cartService.clearCart(userEmail);
            
            redirectAttributes.addFlashAttribute("success", "Pedido realizado com sucesso! Número do pedido: #" + savedOrder.getId());
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao processar pedido: " + e.getMessage());
        }
        
        return "redirect:/";
    }

    @GetMapping("/confirmation")
    public String showConfirmation(Model model, RedirectAttributes redirectAttributes) {
        Order order = (Order) model.getAttribute("order");
        if (order == null) {
            redirectAttributes.addFlashAttribute("error", "Não foi possível encontrar os detalhes do pedido.");
            return "redirect:/";
        }

        // Calcular fecha estimada de entrega (5 días después de la compra)
        LocalDateTime estimatedDeliveryDate = order.getDate().plusDays(5);
        
        // Añadir datos al modelo
        model.addAttribute("order", order);
        model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);
        model.addAttribute("products", order.getProducts());
        model.addAttribute("subtotal", order.getTotalPrice());
        model.addAttribute("shipping", 0.0); // Envío gratis
        model.addAttribute("total", order.getTotalPrice());
        model.addAttribute("status", "Confirmado");
        
        return "checkout-confirmation";
    }
} 