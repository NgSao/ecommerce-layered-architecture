package com.nguyensao.ecommerce_layered_architecture.dto.request;

import com.nguyensao.ecommerce_layered_architecture.enums.OrderStatus;

import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private OrderStatus status;
}