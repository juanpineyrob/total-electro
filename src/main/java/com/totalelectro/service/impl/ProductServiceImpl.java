package com.totalelectro.service.impl;

import com.totalelectro.model.Product;
import com.totalelectro.repository.ProductRepository;
import com.totalelectro.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> searchProducts(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Page<Product> getProductsByCategory(String categorySlug, Pageable pageable) {
        String categorySlugProcessed = processCategorySlug(categorySlug);
        return productRepository.getProductByCategories(categorySlugProcessed, pageable);
    }

    @Override
    public List<Product> getPopularProducts() {
        return productRepository.findAll().stream().limit(10).toList();
    }

    private String processCategorySlug(String categorySlug) {
        return switch (categorySlug) {
            case "iluminacion" -> "Iluminacion";
            case "cables-y-accessorios" -> "Cables y Accesorios";
            case "herramientas-electricas" -> "Herramientas Electricas";
            case "automatizacion-y-domoticas" -> "Automatizacion y Domotica";
            case "seguridad-electrica" -> "Seguridad Electrica";
            default -> categorySlug;
        };
    }
} 