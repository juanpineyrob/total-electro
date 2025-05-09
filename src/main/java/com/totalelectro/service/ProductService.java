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

public interface ProductService {
    // Métodos CRUD básicos
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product saveProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(Long id);

    // Métodos de búsqueda y filtrado
    Page<Product> searchProducts(String name, Pageable pageable);
    Page<Product> getProductsByCategory(String categorySlug, Pageable pageable);
    List<Product> getPopularProducts();
}
