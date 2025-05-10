package com.nguyensao.ecommerce_layered_architecture.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AdminVariantResponse {
    Long id;
    String name;
    String image;
    int stock;
    String color;
    String variant;
    BigDecimal originalPrice;
    BigDecimal salePrice;

}
