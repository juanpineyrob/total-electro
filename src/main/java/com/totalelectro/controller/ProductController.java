package com.totalelectro.controller;

import com.totalelectro.constants.Pages;
import com.totalelectro.constants.PathConstants;
import com.totalelectro.model.Product;
import com.totalelectro.service.ProductService;
import com.totalelectro.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String searchProducts(@RequestParam(required = false) String name,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "12") int size,
                               Model model) {
        Page<Product> products = productService.searchProducts(name, PageRequest.of(page, size));
        model.addAttribute("page", products);
        model.addAttribute("pagination", products.getTotalPages() > 0 ? 
            java.util.stream.IntStream.rangeClosed(0, products.getTotalPages() - 1).boxed().toList() : null);
        return Pages.PRODUCTS;
    }

    @GetMapping("/{id}")
    public String getProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return Pages.PRODUCT;
    }
}
