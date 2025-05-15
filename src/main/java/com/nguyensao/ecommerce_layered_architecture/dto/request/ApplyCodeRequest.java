package com.nguyensao.ecommerce_layered_architecture.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ApplyCodeRequest {
    private BigDecimal orderTotal;
}
