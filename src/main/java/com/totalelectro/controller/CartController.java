package com.totalelectro.controller;

import com.totalelectro.model.CartItem;
import com.totalelectro.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String viewCart(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String userEmail = auth.getName();
        List<CartItem> cartItems = cartService.getCartItems(userEmail);
        Double total = cartService.getCartTotal(userEmail);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", total);
        model.addAttribute("cartItemCount", cartItems.size());

        return "cart/view";
    }

    @PostMapping("/add")
    @ResponseBody
    public String addToCart(@RequestParam Long productId, 
                           @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return "error:Você precisa estar logado para adicionar produtos ao carrinho";
            }

            String userEmail = auth.getName();
            cartService.addToCart(userEmail, productId, quantity);
            
            int cartCount = cartService.getCartItemCount(userEmail);
            return "success:" + cartCount;
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateCart(@RequestParam Long productId, 
                            @RequestParam Integer quantity) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return "error:Usuário não autenticado";
            }

            String userEmail = auth.getName();
            cartService.updateCartItem(userEmail, productId, quantity);
            
            Double total = cartService.getCartTotal(userEmail);
            return "success:" + total;
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    @PostMapping("/remove")
    @ResponseBody
    public String removeFromCart(@RequestParam Long productId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                return "error:Usuário não autenticado";
            }

            String userEmail = auth.getName();
            cartService.removeFromCart(userEmail, productId);
            
            return "success:Item removido do carrinho";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                redirectAttributes.addFlashAttribute("error", "Usuário não autenticado");
                return "redirect:/login";
            }

            String userEmail = auth.getName();
            cartService.clearCart(userEmail);
            
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
} 