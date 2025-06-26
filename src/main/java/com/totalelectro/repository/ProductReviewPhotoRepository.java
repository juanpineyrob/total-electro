package com.totalelectro.repository;

import com.totalelectro.model.ProductReviewPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewPhotoRepository extends JpaRepository<ProductReviewPhoto, Long> {
} 