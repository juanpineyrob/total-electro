package com.totalelectro.repository;

import com.totalelectro.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByUserEmail(String userEmail);
    
    Optional<CartItem> findByUserEmailAndProductId(String userEmail, Long productId);
    
    void deleteByUserEmail(String userEmail);
    
    void deleteByUserEmailAndProductId(String userEmail, Long productId);
    
    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.user.email = :userEmail")
    int countByUserEmail(@Param("userEmail") String userEmail);
    
    @Query("SELECT SUM(c.quantity * CASE WHEN c.product.discountPercent IS NOT NULL AND c.product.discountPercent > 0 " +
           "THEN c.product.price * (1 - c.product.discountPercent / 100.0) " +
           "ELSE c.product.price END) FROM CartItem c WHERE c.user.email = :userEmail")
    Double getTotalByUserEmail(@Param("userEmail") String userEmail);
} 