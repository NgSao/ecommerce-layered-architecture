package com.nguyensao.ecommerce_layered_architecture.dto.request;

import lombok.Data;

@Data
public class VNPayRequestDto {
    private Double amount;
    private String orderId;
    private String returnUrl;
    private String ipAddr;
}
