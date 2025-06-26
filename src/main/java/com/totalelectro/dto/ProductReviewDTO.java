package com.totalelectro.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductReviewDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Long userId;
    private String userName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private List<String> photoUrls;
} 