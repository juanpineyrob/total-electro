package com.totalelectro.service;

import com.totalelectro.dto.ProductReviewDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductReviewService {
    List<ProductReviewDTO> getReviewsByProduct(Long productId);
    List<ProductReviewDTO> getReviewsByUser(String userEmail);
    ProductReviewDTO createReview(Long productId, String userEmail, Integer rating, String comment, List<org.springframework.web.multipart.MultipartFile> photos);
    boolean canUserReviewProduct(String userEmail, Long productId, Long orderId);
    void deleteReviewById(Long reviewId);
} 