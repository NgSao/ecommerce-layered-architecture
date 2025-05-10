package com.nguyensao.ecommerce_layered_architecture.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VariantResponse {
    private Long id;
    private String color;
    private String storage;
    private String sku;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String image;
    private int discount;

}
