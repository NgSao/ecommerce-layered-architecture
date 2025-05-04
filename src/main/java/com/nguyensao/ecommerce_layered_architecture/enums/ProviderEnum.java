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

    public static ProviderEnum fromString(String role) {
        for (ProviderEnum r : ProviderEnum.values()) {
            if (r.getProviderName().equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
}
