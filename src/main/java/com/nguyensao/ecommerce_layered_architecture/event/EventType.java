package com.nguyensao.ecommerce_layered_architecture.event;

public enum EventType {
    REGISTER_OTP,
    VERIFY_OTP,
    FORGOT_PASSWORD,
    RESET_PASSWORD,

    CREATE_INVENTORY,
    UPDATE_INVENTORY,
    DELETE_INVENTORY,
    ORDER_INVENTORY,
    PRODUCT_INVENTORY,

    CREATE_ORDER,
    ORDER,
    USER,
    PRODUCT,
    PRODUCT_CATEGORY,
    PRODUCT_BRAND,
    NEWS,
    PROMOTION
}
