package com.nguyensao.ecommerce_layered_architecture.constant;

public class ApiPathConstant {
    public static final String API_PREFIX = "/api/v1";

    // Auth
    public static final String AUTH_REGISTER = "/public/auth/register";
    public static final String AUTH_VERIFY = "/public/auth/verify";
    public static final String AUTH_SEND_OTP = "/public/auth/send-otp";
    public static final String AUTH_FORGOT_PASSWORD = "/public/auth/forgot-password";
    public static final String AUTH_LOGIN = "/public/auth/login";
    // OAuth2
    public static final String OAUTH2_SUCCESS = "/public/oauth/success";
    public static final String OAUTH2_LINKED_ALREADY = "/public/oauth/linked-already";
    public static final String OAUTH2_LINKED_SUCCESS = "/public/oauth/linked-success";
    public static final String OAUTH2_UNLINK = "/public/oauth/unlinked";

    public static final String LOGOUT = "/public/logout";

    // Admin
    public static final String REFRESH = "/admin/auth/refresh";
    public static final String ADMIN_CREATE = "/admin/users/create";
    public static final String CHANGE_ROLE = "/admin/users/role";
    public static final String CHANGE_STATUS = "/admin/users/active";
    public static final String GET_ALL_USERS = "/admin/users";
    public static final String DELETE_USERS = "/admin/users/delete";

    // Customer
    public static final String CUSTOMER_INFO = "/customer";
    public static final String CUSTOMER_UPDATE = "/customer/updated";
    public static final String RESET_PASSWORD = "/customer/reset-password";

    public static final String ADDRESS_CREATE = "/public/address";
    public static final String ADDRESS_GET_ALL = "/public/address";
    public static final String ADDRESS_ACTIVATE = "/public/address/active/{addressId}";
    public static final String ADDRESS_UPDATE = "/public/address/updated";
    public static final String ADDRESS_DELETE = "/public/address/delete/{addressId}";

}
