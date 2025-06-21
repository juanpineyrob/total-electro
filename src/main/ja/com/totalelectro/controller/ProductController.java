package com.totalelectro.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String getProductsByCategory(@RequestParam String categorySlug,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductsByCategory(categorySlug, pageable);
        
        model.addAttribute("page", products);
        model.addAttribute("categorySlug", categorySlug);
        model.addAttribute("categoryName", getCategoryName(categorySlug));
        model.addAttribute("allCategories", categoryService.getAllCategories());
        model.addAttribute("pagination", products.getTotalPages() > 0 ? 
            java.util.stream.IntStream.rangeClosed(0, products.getTotalPages() - 1).boxed().toList() : null);
        
        return "products";
    }
} 