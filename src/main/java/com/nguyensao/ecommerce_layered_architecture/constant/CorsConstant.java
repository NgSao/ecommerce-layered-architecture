package com.nguyensao.ecommerce_layered_architecture.constant;

import java.util.Arrays;
import java.util.List;

public class CorsConstant {
    public static final List<String> LOCALHOST_FRONTEND = Arrays.asList("http://localhost:8080",
            "http://192.168.72.147:8080", "http://192.168.72.147:8081");

    public static final List<String> ALLOWED_METHODS = Arrays.asList("GET", "POST", "PUT", "DELETE");

    public static final List<String> ALLOWED_HEADERS = Arrays.asList("Authorization", "Cache-Control", "Content-Type");
}
