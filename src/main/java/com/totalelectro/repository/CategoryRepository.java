package com.totalelectro.repository;

import com.totalelectro.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category getCategoryById(Long id);
    Optional<Category> findBySlug(String slug);
}
