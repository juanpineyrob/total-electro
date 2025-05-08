package com.totalelectro.controller;

import com.totalelectro.constants.Pages;
import com.totalelectro.constants.PathConstants;
import com.totalelectro.service.ProductService;
import com.totalelectro.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping(PathConstants.PRODUCT)
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ControllerUtils controllerUtils;

    @GetMapping("/{productId}")
    public String getProductById(@PathVariable Long productId, Model model) {
        model.addAttribute("product", productService.getProductById(productId));
        return Pages.PRODUCT;
    }

    @GetMapping("/category/{categorySlug}")
    public String getCategorySlug(@PathVariable String categorySlug, Model model, Pageable pageable) {
        controllerUtils.addPagination(model, productService.getProductByCategories(categorySlug, pageable));
        return PathConstants.CATEGORY + "/" + categorySlug;
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam String name, Model model, Pageable pageable) {
        controllerUtils.addPagination(model, productService.searchProducts(name, pageable));
        return Pages.PRODUCTS;
    }
}
