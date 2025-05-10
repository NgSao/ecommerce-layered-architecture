package com.nguyensao.ecommerce_layered_architecture.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDto {
    private String fullName;
    private String phone;
    private String addressDetail;
    private String method;
    private double fee;
}