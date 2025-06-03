package com.totalelectro.service.impl;

import com.totalelectro.model.CartItem;
import com.totalelectro.model.Product;
import com.totalelectro.model.User;
import com.totalelectro.repository.CartItemRepository;
import com.totalelectro.service.CartService;
import com.totalelectro.service.ProductService;
import com.totalelectro.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserService userService;

    @Override
    public List<CartItem> getCartItems(String userEmail) {
        return cartItemRepository.findByUserEmail(userEmail);
    }

    @Override
    @Transactional
    public void addToCart(String userEmail, Long productId, Integer quantity) {
        // Verificar se o item já existe no carrinho
        var existingItem = cartItemRepository.findByUserEmailAndProductId(userEmail, productId);
        
        if (existingItem.isPresent()) {
            // Se já existe, incrementar a quantidade
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            // Se não existe, criar novo item
            User user = userService.findByEmail(userEmail);
            Product product = productService.getProductById(productId);
            
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    @Transactional
    public void updateCartItem(String userEmail, Long productId, Integer quantity) {
        var cartItem = cartItemRepository.findByUserEmailAndProductId(userEmail, productId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado no carrinho"));
        
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    @Transactional
    public void removeFromCart(String userEmail, Long productId) {
        cartItemRepository.deleteByUserEmailAndProductId(userEmail, productId);
    }

    @Override
    @Transactional
    public void clearCart(String userEmail) {
        cartItemRepository.deleteByUserEmail(userEmail);
    }

    @Override
    public int getCartItemCount(String userEmail) {
        return cartItemRepository.countByUserEmail(userEmail);
    }

    @Override
    public Double getCartTotal(String userEmail) {
        Double total = cartItemRepository.getTotalByUserEmail(userEmail);
        return total != null ? total : 0.0;
    }

    @Override
    public CartItem findCartItem(String userEmail, Long productId) {
        return cartItemRepository.findByUserEmailAndProductId(userEmail, productId)
                .orElse(null);
    }
} 