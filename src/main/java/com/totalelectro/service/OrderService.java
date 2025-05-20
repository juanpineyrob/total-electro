package com.totalelectro.service;

import com.totalelectro.model.Order;
import java.util.List;

public interface OrderService {
    
    List<Order> findAll();
    
    Order findById(Long id);
    
    List<Order> findByUserEmail(String email);
    
    Order save(Order order, String userEmail);
    
    void deleteById(Long id);
    
    void cancelOrder(Long id);
    
    void completeOrder(Long id);
    
    boolean isOrderOwner(Long orderId, String userEmail);
} 