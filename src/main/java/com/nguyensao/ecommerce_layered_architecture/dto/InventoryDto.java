package com.nguyensao.ecommerce_layered_architecture.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryDto {
    private Long id;
    private String skuProduct;
    private String skuVariant;
    private Integer quantity;
    private Instant lastUpdated;
}