package com.nguyensao.ecommerce_layered_architecture.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZaloPayCallbackRequest {
    private String message;
    private String appTransId;
    private String zaloPayToken;
}