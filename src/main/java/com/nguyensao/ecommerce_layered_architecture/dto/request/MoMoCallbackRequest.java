package com.nguyensao.ecommerce_layered_architecture.dto.request;

import java.util.Map;

import com.nguyensao.ecommerce_layered_architecture.dto.OrderDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoMoCallbackRequest {
    private Map<String, String> momoParams;
    private OrderDto orderDTO;
}
