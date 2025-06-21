package com.totalelectro.controller;

import com.totalelectro.model.Product;
import com.totalelectro.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping("/")
    public String index(Model model) {
        var popularProducts = productService.getPopularProducts();
        model.addAttribute("popularProductsSlide1", popularProducts.subList(0, Math.min(5, popularProducts.size())));
        model.addAttribute("popularProductsSlide2", popularProducts.size() > 5 ? popularProducts.subList(5, Math.min(10, popularProducts.size())) : java.util.Collections.emptyList());
        return "index";
    }

    @GetMapping("/offers")
    public String offers(Model model) {
        return "offers";
    }
} 