package com.nguyensao.ecommerce_layered_architecture.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long productId;
    private Long colorId;

    private String name;
    private double price;
    private int quantity;
    private String color;
    private String storage;
    private String imageUrl;

}