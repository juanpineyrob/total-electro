package com.totalelectro.service;

import com.totalelectro.constants.ErrorMessage;
import com.totalelectro.dto.ProductDTO;
import com.totalelectro.model.Product;

import com.totalelectro.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.PRODUCT_NOT_FOUND));
    }

    public Page<Product> getProductByCategories(String categorySlug, Pageable pageable) {
        String categorySlugProcessed = processCategorySlug(categorySlug);
        return productRepository.getProductByCategories(categorySlugProcessed, pageable);
    }

    public List<Product> getPopularProducts() {
        List<Long> productsIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        return productRepository.findByIdIn(productsIds);
    }

    public Page<Product> searchProducts(String name, Pageable pageable) {
        return productRepository.searchProducts(name, pageable);
    }

    private String processCategorySlug(String categorySlug) {
        switch (categorySlug) {
            case "iluminacion":
                categorySlug = "Iluminacion";
                break;
            case "cables-y-accessorios":
                categorySlug = "Cables y Accesorios";
                break;
            case "herramientas-electricas":
                categorySlug = "Herramientas Electricas";
                break;
            case "automatizacion-y-domoticas":
                categorySlug = "Automatizacion y Domotica";
                break;
            case "seguridad-electrica":
                categorySlug = "Seguridad Electrica";
                break;
        }
        return categorySlug;
    }



}
