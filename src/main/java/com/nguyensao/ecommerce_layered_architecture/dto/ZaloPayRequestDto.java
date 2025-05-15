package com.nguyensao.ecommerce_layered_architecture.dto;

import lombok.Data;

@Data
public class ZaloPayRequestDto {
    private Long amount;
    private String orderId;
}