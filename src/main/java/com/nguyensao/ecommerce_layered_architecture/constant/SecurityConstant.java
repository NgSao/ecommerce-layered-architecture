package com.nguyensao.ecommerce_layered_architecture.constant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

public class SecurityConstant {
    // 1 day (seconds = 24 * 60 * 60)
    public static final long EXPIRATION_TIME = (24 * 60 * 60) * 2;
    public static final long REFRESH_TOKEN_EXP = (24 * 60 * 60) * 12;
    public static final String[] PUBLIC_URLS = {
            "/api/v1/public/**",
            "/v3/api-docs/**", "/swagger-ui/**",
            "/swagger-ui.html" };
    public static final String[] ADMIN_URLS = { "api/v1/admin/**" };

    public static final String OAUTH2_AUTHORIZATION_URL = "/api/v1/oauth2/authorization";
    public static final String OAUTH2_CALLBACK_URL = "/api/v1/oauth2/callback/**";

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    public static long EXPIRATION_OTP = 3 * 60; // 3 phút (3 phút * 60 giây)

    public static final String INVALID_TOKEN = "Invalid token (expired, malformed, or JWT not provided in header).";
    public static final String ACCESS_DENIED = "You do not have permission to access this resource.";
    public static final String TOKEN_REVOKED = "The token has been revoked.";

}
