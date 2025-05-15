package com.nguyensao.ecommerce_layered_architecture.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SePayRequestDto {
    private Double amount;
    private String orderId;
    private String virtualAccount;
}
