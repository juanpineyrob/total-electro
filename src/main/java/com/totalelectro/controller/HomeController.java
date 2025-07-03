package com.totalelectro.controller;

import com.totalelectro.model.Product;
import com.totalelectro.model.Order;
import com.totalelectro.model.OrderStatus;
import com.totalelectro.model.CartItem;
import com.totalelectro.model.User;
import com.totalelectro.service.ProductService;
import com.totalelectro.service.OrderService;
import com.totalelectro.service.CartService;
import com.totalelectro.service.UserService;
import com.totalelectro.repository.ProductRepository;
import com.totalelectro.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @GetMapping("/")
    public String index(Model model) {
        var popularProducts = productService.getPopularProducts();
        model.addAttribute("popularProductsSlide1", popularProducts.subList(0, Math.min(5, popularProducts.size())));
        model.addAttribute("popularProductsSlide2", popularProducts.size() > 5 ? popularProducts.subList(5, Math.min(10, popularProducts.size())) : java.util.Collections.emptyList());
        return "index";
    }

    @GetMapping("/offers")
    public String showOffers(Model model) {
        var offers = productRepository.findDiscountedProducts();
        model.addAttribute("offers", offers);
        return "offers";
    }
    
    @PostMapping("/")
    public String processCheckout(@RequestParam(required = false) String action,
                                 @RequestParam(required = false) String firstName,
                                 @RequestParam(required = false) String lastName,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String phone,
                                 @RequestParam(required = false) String address,
                                 @RequestParam(required = false) String city,
                                 @RequestParam(required = false) String state,
                                 @RequestParam(required = false) String zipCode,
                                 @RequestParam(required = false) String cardNumber,
                                 @RequestParam(required = false) String cardName,
                                 @RequestParam(required = false) String expiryDate,
                                 @RequestParam(required = false) String cvv,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session) {
        
        // Se não for processamento de checkout, apenas mostrar index
        if (!"process-cart".equals(action)) {
            return "redirect:/";
        }
        
        System.out.println("DEBUG: Processando checkout no HomeController!");
        
        // Verificar se o usuário está logado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        
        if (!isAuthenticated) {
            redirectAttributes.addFlashAttribute("error", "Usuário não autenticado");
            return "redirect:/";
        }
        
        String userEmail = auth.getName();
        List<CartItem> cartItems = cartService.getCartItems(userEmail);
        
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Carrinho vazio");
            return "redirect:/";
        }
        
        // VALIDAÇÕES DOS DADOS - TODOS DEVEM ESTAR PREENCHIDOS
        boolean hasErrors = false;
        String errorMessage = "";
        
        // Validar dados pessoais
        if (firstName == null || firstName.trim().isEmpty()) {
            errorMessage += "Nome é obrigatório. ";
            hasErrors = true;
        }
        
        if (lastName == null || lastName.trim().isEmpty()) {
            errorMessage += "Sobrenome é obrigatório. ";
            hasErrors = true;
        }
        
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errorMessage += "E-mail inválido. ";
            hasErrors = true;
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            errorMessage += "Telefone é obrigatório. ";
            hasErrors = true;
        }
        
        // Validar endereço
        if (address == null || address.trim().isEmpty()) {
            errorMessage += "Endereço é obrigatório. ";
            hasErrors = true;
        }
        
        if (city == null || city.trim().isEmpty()) {
            errorMessage += "Cidade é obrigatória. ";
            hasErrors = true;
        }
        
        if (state == null || state.trim().isEmpty()) {
            errorMessage += "Estado é obrigatório. ";
            hasErrors = true;
        }
        
        if (zipCode == null || !zipCode.matches("^\\d{5}-?\\d{3}$")) {
            errorMessage += "CEP inválido. Use o formato 00000-000. ";
            hasErrors = true;
        }
        
        // Validar dados de pagamento
        if (cardNumber == null || cardNumber.replaceAll("\\s", "").length() != 16) {
            errorMessage += "Número do cartão deve ter exatamente 16 dígitos. ";
            hasErrors = true;
        }
        
        if (cardName == null || cardName.trim().isEmpty()) {
            errorMessage += "Nome no cartão é obrigatório. ";
            hasErrors = true;
        }
        
        if (expiryDate == null || !expiryDate.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
            errorMessage += "Data de validade inválida. Use MM/AA. ";
            hasErrors = true;
        }
        
        if (cvv == null || !cvv.matches("^\\d{3,4}$")) {
            errorMessage += "CVV inválido. ";
            hasErrors = true;
        }
        
        // Se há erros, não processar a order
        if (hasErrors) {
            redirectAttributes.addFlashAttribute("error", "Por favor, preencha todos os campos corretamente: " + errorMessage.trim());
            return "redirect:/checkout/cart";
        }
        
        try {
            // Calcular subtotal
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
            
            // Frete
            double shipping = subtotalWithCoupon >= 50.0 ? 0.0 : 50.0;
            Double calculatedShipping = (Double) session.getAttribute("calculatedShippingCost");
            if (calculatedShipping != null) {
                shipping = calculatedShipping;
            }
            double total = subtotalWithCoupon + shipping;
            
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
            
            // Limpar o carrinho
            cartService.clearCart(userEmail);
            
            System.out.println("DEBUG: Pedido criado com sucesso! ID: " + savedOrder.getId());
            redirectAttributes.addFlashAttribute("success", "Pedido realizado com sucesso! Número do pedido: #" + savedOrder.getId());
            
        } catch (Exception e) {
            System.out.println("DEBUG: Erro ao processar pedido: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Erro ao processar pedido: " + e.getMessage());
        }
        
        return "redirect:/";
    }
} 