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
import org.springframework.data.jpa.domain.Specification;

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
    public Page<Product> searchProducts(String name, Double minPrice, Double maxPrice, List<String> categories, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);

        if (name != null && !name.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.upper(root.get("name")), "%" + name.toUpperCase() + "%"));
        }

        if (minPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        if (categories != null && !categories.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.join("category").get("name").in(categories));
        }

        return productRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Product> getProductsByCategory(String categorySlug, Double minPrice, Double maxPrice, Pageable pageable) {
        Specification<Product> spec = Specification.where((root, query, cb) ->
                cb.equal(root.join("category").get("slug"), categorySlug));

        if (minPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        return productRepository.findAll(spec, pageable);
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