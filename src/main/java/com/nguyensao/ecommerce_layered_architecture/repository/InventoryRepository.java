package com.nguyensao.ecommerce_layered_architecture.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nguyensao.ecommerce_layered_architecture.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findBySkuVariant(String skuVariant);

    Optional<Inventory> findBySkuProductAndSkuVariantIsNull(String skuProduct);

    void deleteBySkuVariant(String skuVariant);

    void deleteBySkuProductAndSkuVariantIsNull(String skuProduct);

    List<Inventory> findAllByQuantityGreaterThan(Integer quantity);

    List<Inventory> findAllByQuantityEquals(Integer quantity);

    long count();

    // Đếm số sản phẩm có tồn kho thấp (quantity < threshold)
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.quantity < :threshold")
    long countLowStockProducts(int threshold);
}
