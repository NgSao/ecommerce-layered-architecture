package com.nguyensao.ecommerce_layered_architecture.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.ecommerce_layered_architecture.model.Variant;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {
    Page<Variant> findAll(Pageable pageable);

    Optional<Variant> findVariantBySize(String size);

    Optional<Variant> findByProductIdAndSize(Long productId, String size);

    Optional<Variant> findByIdAndSize(Long id, String size);
}