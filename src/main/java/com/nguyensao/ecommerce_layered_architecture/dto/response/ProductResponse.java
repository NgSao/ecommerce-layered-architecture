package com.nguyensao.ecommerce_layered_architecture.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long id;
    String name;
    String slug;
    String sku;
    String specification;
    String description;
    String promotions;
    BigDecimal originalPrice;
    BigDecimal price;
    Set<String> images = new HashSet<>();
    Set<CategoryResponse> categories = new HashSet<>();
    BrandResponse brand;
    int discount;
    float rating;
    int ratingCount;
    Integer stock;
    Integer sold;
    Set<VariantResponse> variants = new HashSet<>();

    Instant createdAt;

    Instant updatedAt;

    String createdBy;

    String updatedBy;

}
