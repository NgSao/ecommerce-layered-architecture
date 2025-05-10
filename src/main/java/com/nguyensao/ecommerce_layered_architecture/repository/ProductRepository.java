package com.nguyensao.ecommerce_layered_architecture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nguyensao.ecommerce_layered_architecture.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.variants v " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(v.color) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(v.size) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(v.sku) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchProducts(@Param("query") String query);

    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

    List<Product> findByNameContainingIgnoreCase(String query);

    List<Product> findAllByOrderBySoldDesc();

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.salePrice < p.originalPrice")
    List<Product> findBySalePriceLessThanOriginalPriceWithVariants();

}