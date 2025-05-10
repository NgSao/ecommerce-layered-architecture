package com.nguyensao.ecommerce_layered_architecture.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "promotions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Promotion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false, unique = true)
    String code;

    String description;

    @Column(nullable = false)
    String discountType;

    @Column(nullable = false)
    BigDecimal discountValue;

    @Column(nullable = false)
    BigDecimal minOrderValue;

    BigDecimal maxDiscount;

    @Column(nullable = false)
    OffsetDateTime startDate;

    @Column(nullable = false)
    OffsetDateTime endDate;

    @Column(nullable = false)
    boolean isActive;

    @Column(nullable = false)
    int usageLimit;

    @Column(nullable = false)
    int usageCount;
}