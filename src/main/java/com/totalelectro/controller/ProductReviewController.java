package com.totalelectro.controller;

import com.totalelectro.dto.ProductReviewDTO;
import com.totalelectro.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ProductReviewController {
    private final ProductReviewService reviewService;

    @GetMapping("/product/{productId}")
    public List<ProductReviewDTO> getReviewsByProduct(@PathVariable Long productId) {
        return reviewService.getReviewsByProduct(productId);
    }

    @GetMapping("/user")
    public List<ProductReviewDTO> getReviewsByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        return reviewService.getReviewsByUser(userEmail);
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<?> createReview(
            @PathVariable Long productId,
            @RequestParam int rating,
            @RequestParam String comment,
            @RequestParam(required = false) List<MultipartFile> photos
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return ResponseEntity.status(403).body("Usuário não autenticado");
        }
        
        String userEmail = auth.getName();
        
        try {
            ProductReviewDTO dto = reviewService.createReview(productId, userEmail, rating, comment, photos);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/can-review/product/{productId}/order/{orderId}")
    public boolean canUserReview(@PathVariable Long productId, @PathVariable Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        return reviewService.canUserReviewProduct(userEmail, productId, orderId);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser") ||
            auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body("Apenas administradores podem apagar avaliações.");
        }
        reviewService.deleteReviewById(reviewId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API Reviews funcionando!");
    }
} 