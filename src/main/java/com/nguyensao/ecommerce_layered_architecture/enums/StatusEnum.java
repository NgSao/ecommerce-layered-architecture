
package com.nguyensao.ecommerce_layered_architecture.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    INACTIVE("Inactive"),
    ACTIVE("Active"),
    BLOCKED("Blocked");

    private final String statusName;
}
