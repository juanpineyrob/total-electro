package com.totalelectro.service;

import com.totalelectro.model.CartItem;
import java.util.List;

public interface CartService {
    
    List<CartItem> getCartItems(String userEmail);
    
    void addToCart(String userEmail, Long productId, Integer quantity);
    
    void updateCartItem(String userEmail, Long productId, Integer quantity);
    
    void removeFromCart(String userEmail, Long productId);
    
    void clearCart(String userEmail);
    
    int getCartItemCount(String userEmail);
    
    Double getCartTotal(String userEmail);
    
    CartItem findCartItem(String userEmail, Long productId);
} 