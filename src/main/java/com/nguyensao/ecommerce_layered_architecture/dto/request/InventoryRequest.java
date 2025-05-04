package com.nguyensao.ecommerce_layered_architecture.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryRequest {
    private String skuProduct;
    private String skuVariant;
    private Integer quantity;
}
