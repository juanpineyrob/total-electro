package com.totalelectro.service.impl;

import com.totalelectro.dto.ProductReviewDTO;
import com.totalelectro.model.*;
import com.totalelectro.repository.ProductReviewPhotoRepository;
import com.totalelectro.repository.ProductReviewRepository;
import com.totalelectro.repository.OrderRepository;
import com.totalelectro.repository.UserRepository;
import com.totalelectro.repository.ProductRepository;
import com.totalelectro.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {
    private final ProductReviewRepository reviewRepository;
    private final ProductReviewPhotoRepository photoRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public List<ProductReviewDTO> getReviewsByProduct(Long productId) {
        List<ProductReview> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        return reviews.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductReviewDTO> getReviewsByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> 
            new RuntimeException("Usuário não encontrado: " + userEmail));
        List<ProductReview> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return reviews.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductReviewDTO createReview(Long productId, String userEmail, Integer rating, String comment, List<MultipartFile> photos) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> 
            new RuntimeException("Usuário não encontrado: " + userEmail));
        Product product = productRepository.findById(productId).orElseThrow();
        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setUser(user);
        review.setOrder(null); // Não exige mais order
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        review.setPhotos(new ArrayList<>());
        ProductReview saved = reviewRepository.save(review);
        // Mock do upload de fotos (salva apenas o nome original)
        if (photos != null) {
            for (MultipartFile file : photos) {
                if (!file.isEmpty()) {
                    ProductReviewPhoto photo = new ProductReviewPhoto();
                    photo.setReview(saved);
                    photo.setUrl("/uploads/reviews/" + file.getOriginalFilename());
                    photoRepository.save(photo);
                    saved.getPhotos().add(photo);
                }
            }
        }
        return toDTO(saved);
    }

    @Override
    public boolean canUserReviewProduct(String userEmail, Long productId, Long orderId) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> 
            new RuntimeException("Usuário não encontrado: " + userEmail));
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || !order.getUser().getId().equals(user.getId())) return false;
        if (!order.getStatus().name().equalsIgnoreCase("COMPLETED")) return false;
        if (reviewRepository.existsByProductIdAndUserIdAndOrderId(productId, user.getId(), orderId)) return false;
        // Verifica se o produto faz parte do pedido
        boolean hasProduct = order.getProducts().stream().anyMatch(p -> p.getId().equals(productId));
        return hasProduct;
    }

    @Override
    public void deleteReviewById(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    private ProductReviewDTO toDTO(ProductReview review) {
        ProductReviewDTO dto = new ProductReviewDTO();
        dto.setId(review.getId());
        dto.setProductId(review.getProduct().getId());
        dto.setProductName(review.getProduct().getName());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getFirstName() + " " + review.getUser().getLastName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setPhotoUrls(review.getPhotos() != null ? review.getPhotos().stream().map(ProductReviewPhoto::getUrl).collect(Collectors.toList()) : new ArrayList<>());
        return dto;
    }
} 