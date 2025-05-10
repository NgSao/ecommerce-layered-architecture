package com.nguyensao.ecommerce_layered_architecture.dto.response;

import java.math.BigDecimal;
import java.util.Set;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AdminProductResponse {
    Long id;
    String name;
    String image;
    BigDecimal originalPrice;
    BigDecimal salePrice;
    int stock;
    Set<AdminVariantResponse> variants;

}
