package com.totalelectro.repository;

import com.totalelectro.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByIdIn(List<Long> productsIds);

    Page<Product> findAllByOrderByPriceAsc(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE (:categorySlug IS NULL OR p.category.name = :categorySlug)")
    Page<Product> getProductByCategories(@Param("categorySlug") String categorySlug, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE UPPER(p.name) LIKE UPPER(CONCAT('%', :name, '%'))  "
    )
    Page<Product> searchProducts(String name, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
