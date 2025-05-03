package com.nguyensao.ecommerce_layered_architecture.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2LinkRequest {
    private String email;
    private Long id;
}
