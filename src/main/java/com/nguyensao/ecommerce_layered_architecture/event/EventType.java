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

    KAFKA_ORDER,

    ORDER_NOTIFICATION,
    USER_NOTIFICATION,
    PRODUCT_NOTIFICATION,
    NEWS_NOTIFICATION,
    PROMOTION_NOTIFICATION,

    PRODUCT_RATING,

    FILE_REVIEWS,
    FILE_AVATAR,
    FILE_PRODUCT
}
