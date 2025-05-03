package com.nguyensao.ecommerce_layered_architecture.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleAuthorities {
    CUSTOMER("Customer"),
    STAFF("Staff"),
    ADMIN("Admin");

    private final String role;

    public static RoleAuthorities fromString(String role) {
        for (RoleAuthorities r : RoleAuthorities.values()) {
            if (r.getRole().equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
}
