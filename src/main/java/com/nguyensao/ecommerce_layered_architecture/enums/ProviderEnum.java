package com.nguyensao.ecommerce_layered_architecture.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderEnum {
    LOCAL("Local"),
    FACEBOOK("Facebook"),
    GOOGLE("Google");

    private final String providerName;
}
