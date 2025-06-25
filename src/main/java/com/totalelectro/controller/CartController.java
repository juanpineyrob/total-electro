package com.totalelectro.controller;

import com.totalelectro.model.CartItem;
import com.totalelectro.service.CartService;
import com.totalelectro.service.ShippingService;
import com.totalelectro.dto.ShippingCalculationDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import com.totalelectro.repository.CategoryRepository;
import com.totalelectro.model.Product;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ShippingService shippingService;
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String userEmail = auth.getName();
        List<CartItem> cartItems = cartService.getCartItems(userEmail);
        double subtotal = cartService.getCartTotal(userEmail);

        // CUPOM
        String appliedCoupon = (String) session.getAttribute("appliedCoupon");
        Double discountTotal = 0.0;
        if (appliedCoupon != null && appliedCoupon.equalsIgnoreCase("LUZ15")) {
            // Buscar categoria Iluminacion
            var catOpt = categoryRepository.findBySlug("iluminacion");
            if (catOpt.isPresent()) {
                Long iluminacionId = catOpt.get().getId();
                for (CartItem item : cartItems) {
                    Product p = item.getProduct();
                    if (p.getCategory().getId().equals(iluminacionId)) {
                        discountTotal += (p.getPrice().doubleValue() * 0.15) * item.getQuantity();
                    }
                }
            }
        }
        double total = subtotal - discountTotal;
        if (total < 0) total = 0;

        final double FREE_SHIPPING_THRESHOLD = shippingService.getFreeShippingThreshold();
        double shippingCost = 0.0;
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shippingCost", shippingCost);
        model.addAttribute("cartTotal", total + shippingCost);
        model.addAttribute("freeShippingThreshold", FREE_SHIPPING_THRESHOLD);
        model.addAttribute("isFreeShipping", subtotal >= FREE_SHIPPING_THRESHOLD);
        model.addAttribute("cartItemCount", cartItems.size());
        model.addAttribute("appliedCoupon", appliedCoupon);
        if (discountTotal > 0) model.addAttribute("discountTotal", discountTotal);
        // Mensagens de cupom
        String couponMessage = (String) session.getAttribute("couponMessage");
        Boolean couponSuccess = (Boolean) session.getAttribute("couponSuccess");
        if (couponMessage != null) {
            model.addAttribute("couponMessage", couponMessage);
            model.addAttribute("couponSuccess", couponSuccess != null && couponSuccess);
            session.removeAttribute("couponMessage");
            session.removeAttribute("couponSuccess");
        }
        return "cart/view";
    }

    @PostMapping("/calculate-shipping")
    @ResponseBody
    public ResponseEntity<ShippingCalculationDTO> calculateShipping(@RequestBody Map<String, String> request) {
        ShippingCalculationDTO result = new ShippingCalculationDTO();

        try {
            String destinationCep = request.get("destinationAddress");

            if (destinationCep == null || destinationCep.isBlank()) {
                result.setErrorMessage("CEP de destino é obrigatório.");
                return ResponseEntity.badRequest().body(result);
            }

            double distance = shippingService.getDistance(destinationCep);
            double shippingCost = shippingService.calculateShipping(destinationCep);
            String estimatedDeliveryTime = calculateEstimatedDeliveryTime(distance);

            result.setOriginAddress("CEP: " + shippingService.getStoreCep());
            result.setDestinationAddress(destinationCep);
            result.setShippingCost(Math.round(shippingCost * 100.0) / 100.0);
            result.setEstimatedDeliveryTime(estimatedDeliveryTime);
            result.setFreeShipping(shippingCost == 0);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Erro no cálculo do frete", e);
            String message = e.getMessage();
            if (message == null || message.isBlank()) {
                message = "Não foi possível calcular o frete. Verifique o CEP e tente novamente.";
            }
            result.setErrorMessage(message);
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/test-shipping/{cep}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testShipping(@PathVariable String cep) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            logger.info("Testando cálculo de frete para CEP: {}", cep);
            
            double distance = shippingService.getDistance(cep);
            double shippingCost = shippingService.calculateShipping(cep);
            String estimatedDeliveryTime = calculateEstimatedDeliveryTime(distance);
            
            result.put("success", true);
            result.put("cep", cep);
            result.put("shippingCost", Math.round(shippingCost * 100.0) / 100.0);
            result.put("estimatedDeliveryTime", estimatedDeliveryTime);
            result.put("storeCep", shippingService.getStoreCep());
            result.put("baseRate", shippingService.getBaseRate());
            result.put("ratePerKm", shippingService.getRatePerKm());
            result.put("maxDistance", shippingService.getMaxDistance());
            
            logger.info("Cálculo de frete realizado com sucesso: custo=R${}", 
                       result.get("shippingCost"));
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erro no teste de frete para CEP {}: {}", cep, e.getMessage(), e);
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("cep", cep);
            result.put("storeCep", shippingService.getStoreCep());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/add")
    @ResponseBody
    public String addToCart(@RequestParam Long productId, 
                           @RequestParam(defaultValue = "1") Integer quantity,
                           HttpSession session) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return "error:Você precisa estar logado para adicionar produtos ao carrinho";
            }

            String userEmail = auth.getName();
            cartService.addToCart(userEmail, productId, quantity);
            
            int cartCount = cartService.getCartItemCount(userEmail);
            session.setAttribute("cartItemCount", cartCount);
            return "success:" + cartCount;
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateCart(@RequestParam Long productId, 
                            @RequestParam Integer quantity,
                            HttpSession session) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return "error:Usuário não autenticado";
            }

            String userEmail = auth.getName();
            cartService.updateCartItem(userEmail, productId, quantity);
            
            Double total = cartService.getCartTotal(userEmail);
            session.setAttribute("cartItemCount", cartService.getCartItemCount(userEmail));
            return "success:" + total;
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    @PostMapping("/remove")
    @ResponseBody
    public String removeFromCart(@RequestParam Long productId, HttpSession session) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return "error:Usuário não autenticado";
            }

            String userEmail = auth.getName();
            cartService.removeFromCart(userEmail, productId);
            session.setAttribute("cartItemCount", cartService.getCartItemCount(userEmail));
            
            return "success:Item removido do carrinho";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                redirectAttributes.addFlashAttribute("error", "Usuário não autenticado");
                return "redirect:/login";
            }

            String userEmail = auth.getName();
            cartService.clearCart(userEmail);
            session.setAttribute("cartItemCount", 0);
            
            redirectAttributes.addFlashAttribute("success", "Carrinho limpo com sucesso");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao limpar carrinho: " + e.getMessage());
        }
        
        return "redirect:/cart";
    }

    @GetMapping("/count")
    @ResponseBody
    public int getCartCount() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return 0;
            }

            String userEmail = auth.getName();
            return cartService.getCartItemCount(userEmail);
        } catch (Exception e) {
            return 0;
        }
    }

    @GetMapping("/checkout")
    public String goToCheckout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        return "redirect:/checkout/cart";
    }

    @PostMapping("/apply-coupon")
    public String applyCoupon(@RequestParam String coupon, HttpSession session, RedirectAttributes redirectAttributes) {
        if (coupon == null || coupon.isBlank()) {
            session.setAttribute("couponMessage", "Digite um cupom válido.");
            session.setAttribute("couponSuccess", false);
            return "redirect:/cart";
        }
        if (coupon.equalsIgnoreCase("LUZ15")) {
            session.setAttribute("appliedCoupon", coupon.toUpperCase());
            session.setAttribute("couponMessage", "Cupom aplicado: 15% OFF em Iluminação!");
            session.setAttribute("couponSuccess", true);
        } else {
            session.removeAttribute("appliedCoupon");
            session.setAttribute("couponMessage", "Cupom inválido ou não suportado.");
            session.setAttribute("couponSuccess", false);
        }
        return "redirect:/cart";
    }
    
    @PostMapping("/save-shipping")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveShipping(@RequestBody Map<String, Object> request, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Double shippingCost = (Double) request.get("shippingCost");
            String estimatedDeliveryTime = (String) request.get("estimatedDeliveryTime");
            String destinationCep = (String) request.get("destinationCep");
            
            if (shippingCost != null) {
                session.setAttribute("calculatedShippingCost", shippingCost);
                session.setAttribute("calculatedDeliveryTime", estimatedDeliveryTime);
                session.setAttribute("calculatedDestinationCep", destinationCep);
                
                result.put("success", true);
                result.put("message", "Frete salvo com sucesso");
            } else {
                result.put("success", false);
                result.put("error", "Valor do frete não fornecido");
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Erro ao salvar frete: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    @PostMapping("/test-clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testClear() {
        Map<String, Object> result = new HashMap<>();
        
        logger.info("Endpoint /test-clear chamado");
        
        result.put("success", true);
        result.put("message", "Teste funcionando");
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/clear-shipping")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearShipping(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        logger.info("Endpoint /clear-shipping chamado");
        
        try {
            logger.info("Removendo atributos de frete da sessão");
            session.removeAttribute("calculatedShippingCost");
            session.removeAttribute("calculatedDeliveryTime");
            session.removeAttribute("calculatedDestinationCep");
            
            result.put("success", true);
            result.put("message", "Frete removido com sucesso");
            
            logger.info("Frete removido com sucesso da sessão");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Erro ao remover frete da sessão", e);
            result.put("success", false);
            result.put("error", "Erro ao remover frete: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    private String calculateEstimatedDeliveryTime(double distance) {
        if (distance <= 5) {
            return "1-2 dias úteis";
        } else if (distance <= 20) {
            return "2-3 dias úteis";
        } else if (distance <= 50) {
            return "3-5 dias úteis";
        } else if (distance <= 100) {
            return "5-7 dias úteis";
        } else {
            return "7-10 dias úteis";
        }
    }
} 