package com.totalelectro.service;

import com.totalelectro.constants.ErrorMessage;
import com.totalelectro.constants.Pages;
import com.totalelectro.dto.CategoryDTO;
import com.totalelectro.dto.ProductDTO;
import com.totalelectro.model.Category;
import com.totalelectro.model.Product;
import com.totalelectro.repository.CategoryRepository;
import com.totalelectro.repository.ProductRepository;

import com.totalelectro.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ControllerUtils controllerUtils;

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAllByOrderByPriceAsc(pageable);
    }

    public Page<Product> searchProducts(String name, Pageable pageable) {
        return productRepository.searchProducts(name, pageable);
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public ResponseEntity<ProductDTO> insert(ProductDTO productDTO, BindingResult bindingResult, Model model) {
        if(controllerUtils.validateInputFields(bindingResult, model, "product", productDTO)) {
            return ResponseEntity.badRequest().build();
        }
        Product entity = new Product();
        copyProductDTOToEntity(productDTO, entity);

        return ResponseEntity.ok(new ProductDTO(entity));
    }

    private void copyProductDTOToEntity(ProductDTO dto, Product entity) {
        entity.setShort_description(dto.getShort_description());
        entity.setLong_description(dto.getLong_description());
        entity.setPrice(dto.getPrice());
        entity.setDimensions(dto.getDimensions());
        entity.setImageUrl(dto.getImageUrl());
        Category category = new Category();
        category.setId(dto.getCategory().getId());
        entity.setCategory(categoryRepository.getCategoryById(category.getId()));
    }
}
