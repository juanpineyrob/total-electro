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
import jakarta.servlet.http.HttpSession;
import com.totalelectro.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String showCheckout(@RequestParam(required = false) Long productId,
                              @RequestParam(defaultValue = "1") int quantity,
                              Model model) {
        
        // Verificar se o usuário está autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        
        // Se não há produto específico, redirecionar para o carrinho
        if (productId == null) {
            if (isAuthenticated) {
                return "redirect:/checkout/cart";
            } else {
                return "redirect:/cart";
            }
        }
        
        User user = null;
        if (isAuthenticated) {
            user = userService.findByEmail(auth.getName());
        }
        
        // Buscar o produto
        Product product = productService.getProductById(productId);
        if (product == null) {
            return "redirect:/";
        }
        
        // Calcular valores
        double subtotal = product.getPrice().doubleValue() * quantity;
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
    public String showCartCheckout(Model model, HttpSession session) {
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
        // Calcular subtotal com desconto dos produtos
        double subtotal = 0.0;
        for (CartItem item : cartItems) {
            double price = item.getProduct().getPrice().doubleValue();
            Double discount = item.getProduct().getDiscountPercent();
            if (discount != null && discount > 0) {
                price = price * (1 - discount / 100.0);
            }
            subtotal += price * item.getQuantity();
        }
        // Cupom LUZ15
        String appliedCoupon = (String) session.getAttribute("appliedCoupon");
        Double discountTotal = 0.0;
        if (appliedCoupon != null && appliedCoupon.equalsIgnoreCase("LUZ15")) {
            var catOpt = categoryRepository.findBySlug("iluminacion");
            if (catOpt.isPresent()) {
                Long iluminacionId = catOpt.get().getId();
                for (CartItem item : cartItems) {
                    Product p = item.getProduct();
                    if (p.getCategory().getId().equals(iluminacionId)) {
                        double basePrice = p.getPrice().doubleValue();
                        Double prodDiscount = p.getDiscountPercent();
                        if (prodDiscount != null && prodDiscount > 0) {
                            basePrice = basePrice * (1 - prodDiscount / 100.0);
                        }
                        discountTotal += (basePrice * 0.15) * item.getQuantity();
                    }
                }
            }
        }
        double subtotalWithCoupon = subtotal - discountTotal;
        if (subtotalWithCoupon < 0) subtotalWithCoupon = 0;
        // Frete grátis se subtotal (com descontos) >= 50
        double shipping = subtotalWithCoupon >= 50.0 ? 0.0 : 50.0;
        double tax = subtotalWithCoupon * 0.21; // 21% IVA
        double total = subtotalWithCoupon + shipping + tax;
        model.addAttribute("user", user);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discountTotal", discountTotal > 0 ? discountTotal : null);
        model.addAttribute("shipping", shipping);
        model.addAttribute("tax", tax);
        model.addAttribute("total", total);
        model.addAttribute("isCartCheckout", true);
        model.addAttribute("appliedCoupon", appliedCoupon);
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

    @PostMapping("/process-cart")
    public String processCartCheckout(@RequestParam String firstName,
                                     @RequestParam String lastName,
                                     @RequestParam String email,
                                     @RequestParam String phone,
                                     @RequestParam String address,
                                     @RequestParam String city,
                                     @RequestParam String state,
                                     @RequestParam String zipCode,
                                     RedirectAttributes redirectAttributes,
                                     HttpSession session) {
        
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
            
            // Calcular subtotal com desconto dos produtos (igual ao showCartCheckout)
            double subtotal = 0.0;
            for (CartItem item : cartItems) {
                double price = item.getProduct().getPrice().doubleValue();
                Double discount = item.getProduct().getDiscountPercent();
                if (discount != null && discount > 0) {
                    price = price * (1 - discount / 100.0);
                }
                subtotal += price * item.getQuantity();
            }
            
            // Cupom LUZ15
            String appliedCoupon = (String) session.getAttribute("appliedCoupon");
            Double discountTotal = 0.0;
            if (appliedCoupon != null && appliedCoupon.equalsIgnoreCase("LUZ15")) {
                var catOpt = categoryRepository.findBySlug("iluminacion");
                if (catOpt.isPresent()) {
                    Long iluminacionId = catOpt.get().getId();
                    for (CartItem item : cartItems) {
                        Product p = item.getProduct();
                        if (p.getCategory().getId().equals(iluminacionId)) {
                            double basePrice = p.getPrice().doubleValue();
                            Double prodDiscount = p.getDiscountPercent();
                            if (prodDiscount != null && prodDiscount > 0) {
                                basePrice = basePrice * (1 - prodDiscount / 100.0);
                            }
                            discountTotal += (basePrice * 0.15) * item.getQuantity();
                        }
                    }
                }
            }
            double subtotalWithCoupon = subtotal - discountTotal;
            if (subtotalWithCoupon < 0) subtotalWithCoupon = 0;
            
            // Frete grátis se subtotal (com descontos) >= 50
            double shipping = subtotalWithCoupon >= 50.0 ? 0.0 : 50.0;
            double tax = subtotalWithCoupon * 0.21; // 21% IVA
            double total = subtotalWithCoupon + shipping + tax;
            
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
} 