package com.nguyensao.ecommerce_layered_architecture.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoMoRequestDto {
    private Double amount;
    private String orderId;
    private String ipAddr;

}
