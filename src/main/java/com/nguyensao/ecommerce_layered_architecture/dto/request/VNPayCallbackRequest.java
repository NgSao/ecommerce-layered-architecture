package com.nguyensao.ecommerce_layered_architecture.dto.request;

import java.util.Map;

import com.nguyensao.ecommerce_layered_architecture.dto.OrderDto;

import lombok.Data;

@Data
public class VNPayCallbackRequest {
    private Map<String, String> vnpayParams;
    private OrderDto orderDTO;
}
