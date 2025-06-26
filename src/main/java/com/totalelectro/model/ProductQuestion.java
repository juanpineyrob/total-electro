package com.totalelectro.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 1000)
    private String question;
    
    @Column(length = 1000)
    private String answer;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "answered_by")
    private User answeredBy;
    
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_answered", nullable = false)
    private Boolean isAnswered = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 