package com.totalelectro.service.impl;

import com.totalelectro.exception.OrderNotFoundException;
import com.totalelectro.exception.UserNotFoundException;
import com.totalelectro.model.Order;
import com.totalelectro.model.OrderStatus;
import com.totalelectro.model.User;
import com.totalelectro.repository.OrderRepository;
import com.totalelectro.repository.UserRepository;
import com.totalelectro.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findAllWithProducts() {
        return orderRepository.findAllWithProducts();
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("No se encontró la orden con ID: " + id));
    }

    @Override
    public Order findByIdWithProducts(Long id) {
        return orderRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new OrderNotFoundException("No se encontró la orden con ID: " + id));
    }

    @Override
    public List<Order> findByUserEmail(String email) {
        return orderRepository.findAllByUserEmailOrderByDateDesc(email);
    }

    @Override
    @Transactional
    public Order save(Order order, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario con email: " + userEmail));
        
        order.setUser(user);
        order.setDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDIENTE);
        
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("No se encontró la orden con ID: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = findById(id);
        if (OrderStatus.COMPLETADA.equals(order.getStatus())) {
            throw new IllegalStateException("No se puede cancelar una orden completada");
        }
        order.setStatus(OrderStatus.CANCELADA);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void completeOrder(Long id) {
        Order order = findById(id);
        if (OrderStatus.CANCELADA.equals(order.getStatus())) {
            throw new IllegalStateException("No se puede completar una orden cancelada");
        }
        order.setStatus(OrderStatus.COMPLETADA);
        orderRepository.save(order);
    }

    @Override
    public boolean isOrderOwner(Long orderId, String userEmail) {
        return orderRepository.existsByIdAndUserEmail(orderId, userEmail);
    }

    @Override
    public Double getCurrentMonthSales() {
        return orderRepository.findCurrentMonthCompletedOrdersTotal();
    }
} 