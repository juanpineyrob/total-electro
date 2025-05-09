package com.totalelectro.controller;

import com.totalelectro.constants.Pages;
import com.totalelectro.constants.PathConstants;
import com.totalelectro.model.Product;
import com.totalelectro.service.ProductService;
import com.totalelectro.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(PathConstants.PRODUCT)
public class ProductController {

    private final ProductService productService;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductsByCategory(categorySlug, pageable);
        
        model.addAttribute("page", products);
        model.addAttribute("categorySlug", categorySlug);
        model.addAttribute("pagination", products.getTotalPages() > 0 ? 
            java.util.stream.IntStream.rangeClosed(0, products.getTotalPages() - 1).boxed().toList() : null);
        
        return Pages.PRODUCTS;
    }

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.searchProducts(name, pageable);
        
        model.addAttribute("page", products);
        model.addAttribute("searchQuery", name);
        model.addAttribute("pagination", products.getTotalPages() > 0 ? 
            java.util.stream.IntStream.rangeClosed(0, products.getTotalPages() - 1).boxed().toList() : null);
        
        return Pages.PRODUCTS;
    }

    @GetMapping("/popular")
    public String getPopularProducts(Model model) {
        model.addAttribute("products", productService.getPopularProducts());
        return Pages.PRODUCTS;
    }
}
