package com.nguyensao.ecommerce_layered_architecture.utils;

import java.util.UUID;

public class GenerateOrder {

    public static String generateOrderCode() {
        return "MT." + UUID.randomUUID().toString().replace("-", "").substring(0,
                6).toUpperCase();
    }
}
