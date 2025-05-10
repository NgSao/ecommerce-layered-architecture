package com.nguyensao.ecommerce_layered_architecture.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyensao.ecommerce_layered_architecture.model.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    boolean existsByCode(String code);

    Optional<Promotion> findByCode(String code);

    Optional<Promotion> findFirstByIsActiveTrueAndEndDateAfterOrderByStartDateDesc(OffsetDateTime now);

    List<Promotion> findByIsActiveTrueAndEndDateAfterOrderByStartDateDesc(OffsetDateTime now);

}