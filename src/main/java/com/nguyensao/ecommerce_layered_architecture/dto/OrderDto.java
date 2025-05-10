package com.nguyensao.ecommerce_layered_architecture.dto;

import lombok.*;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

import com.nguyensao.ecommerce_layered_architecture.enums.OrderStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String userId;
    private Set<OrderItemDto> items;
    private ShippingDto shipping;
    private PaymentDto payment;
    private String promoCode;
    private double discount;
    private double total;
    private String note;
    private OrderStatus orderStatus;
    private Instant createdAt;
    private Instant updatedAt;
    private String orderCode;

}