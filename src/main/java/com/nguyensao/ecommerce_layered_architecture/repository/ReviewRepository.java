package com.nguyensao.ecommerce_layered_architecture.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyensao.ecommerce_layered_architecture.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);

    Optional<Review> findByUserIdAndProductId(String userId, Long productId);

}