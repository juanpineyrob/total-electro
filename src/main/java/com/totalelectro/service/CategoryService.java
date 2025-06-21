package com.totalelectro.service;

import com.totalelectro.dto.CategoryDTO;
import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategoryBySlug(String slug);
} 