package com.nguyensao.ecommerce_layered_architecture.dto;

import lombok.Data;
import lombok.Builder;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private String method;
    private String status;
}