package com.nguyensao.ecommerce_layered_architecture.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SePayWebhookDto {
    private Long id;
    private String gateway;
    private String transactionDate;
    private String accountNumber;
    private String subAccount;
    private String code;
    private String content;
    private String transferType;
    private String description;
    private Double transferAmount;
    private String referenceCode;
    private Integer accumulated;
}
