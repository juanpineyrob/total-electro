package com.totalelectro.repository;

import com.totalelectro.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUserEmail(String email);
    
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o WHERE o.id = :orderId AND o.user.email = :userEmail")
    boolean existsByIdAndUserEmail(@Param("orderId") Long orderId, @Param("userEmail") String userEmail);
    
    @Query("SELECT o FROM Order o WHERE o.user.email = :email ORDER BY o.date DESC")
    List<Order> findAllByUserEmailOrderByDateDesc(@Param("email") String email);
    
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.products LEFT JOIN FETCH o.user ORDER BY o.date DESC")
    List<Order> findAllWithProducts();
    
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.products LEFT JOIN FETCH o.user WHERE o.id = :id")
    Optional<Order> findByIdWithProducts(@Param("id") Long id);
    
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0.0) FROM Order o WHERE o.status = 'COMPLETADA' AND YEAR(o.date) = YEAR(CURRENT_DATE) AND MONTH(o.date) = MONTH(CURRENT_DATE)")
    Double findCurrentMonthCompletedOrdersTotal();
} 