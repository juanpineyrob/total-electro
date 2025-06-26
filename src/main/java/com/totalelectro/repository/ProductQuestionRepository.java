package com.totalelectro.repository;

import com.totalelectro.model.ProductQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductQuestionRepository extends JpaRepository<ProductQuestion, Long> {
    
    List<ProductQuestion> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    List<ProductQuestion> findByProductIdAndIsAnsweredOrderByCreatedAtDesc(Long productId, Boolean isAnswered);
    
    @Query("SELECT pq FROM ProductQuestion pq WHERE pq.product.id = :productId ORDER BY pq.isAnswered ASC, pq.createdAt DESC")
    List<ProductQuestion> findByProductIdOrderByAnsweredAndDate(@Param("productId") Long productId);
    
    List<ProductQuestion> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT COUNT(pq) FROM ProductQuestion pq WHERE pq.product.id = :productId AND pq.isAnswered = false")
    Long countUnansweredQuestionsByProductId(@Param("productId") Long productId);
} 