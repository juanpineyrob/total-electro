package com.totalelectro.controller;

import com.totalelectro.constants.Pages;
import com.totalelectro.constants.PathConstants;
import com.totalelectro.dto.CategoryDTO;
import com.totalelectro.model.Product;
import com.totalelectro.service.ProductService;
import com.totalelectro.service.CategoryService;
import com.totalelectro.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(PathConstants.PRODUCT)
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ControllerUtils controllerUtils;

    @GetMapping("/{id}")
    public String getProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return Pages.PRODUCT;
    }

    @GetMapping("/category/{categorySlug}")
    public String getProductsByCategory(
            @PathVariable String categorySlug,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "relevance") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        CategoryDTO category = categoryService.getCategoryBySlug(categorySlug);
        Sort sorting = getSort(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Product> products = productService.getProductsByCategory(categorySlug, minPrice, maxPrice, pageable);
        
        model.addAttribute("page", products);
        model.addAttribute("categorySlug", categorySlug);
        model.addAttribute("categoryName", category.getName());
        model.addAttribute("allCategories", categoryService.getAllCategories());
        model.addAttribute("pagination", products.getTotalPages() > 0 ?
            java.util.stream.IntStream.rangeClosed(0, products.getTotalPages() - 1).boxed().toList() : null);
        
        return "category/products";
    }

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(defaultValue = "relevance") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        Sort sorting = getSort(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Product> products = productService.searchProducts(name, minPrice, maxPrice, categories, pageable);

        model.addAttribute("page", products);
        model.addAttribute("allCategories", categoryService.getAllCategories());
        model.addAttribute("searchQuery", name);
        model.addAttribute("pagination", products.getTotalPages() > 0 ?
            java.util.stream.IntStream.rangeClosed(0, products.getTotalPages() - 1).boxed().toList() : null);

        return Pages.PRODUCTS;
    }

    private Sort getSort(String sort) {
        switch (sort) {
            case "price-asc":
                return Sort.by("price").ascending();
            case "price-desc":
                return Sort.by("price").descending();
            case "name-asc":
                return Sort.by("name").ascending();
            case "name-desc":
                return Sort.by("name").descending();
            default: // relevance
                return Sort.unsorted();
        }
    }

    @GetMapping("/popular")
    public String getPopularProducts(Model model) {
        model.addAttribute("products", productService.getPopularProducts());
        return Pages.PRODUCTS;
    }
}
