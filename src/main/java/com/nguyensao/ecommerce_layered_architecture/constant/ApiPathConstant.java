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
    public static final String OAUTH2_UNLINK = "/oauth/unlinked";

    public static final String LOGOUT = "/auth/logout";

    // Admin
    public static final String REFRESH = "/admin/auth/refresh";

    public static final String ADMIN_CREATE = "/admin/users/create";
    public static final String CHANGE_ROLE = "/admin/users/role";
    public static final String CHANGE_STATUS = "/admin/users/active";
    public static final String GET_ALL_USERS = "/admin/users";
    public static final String DELETE_USERS = "/admin/users/delete";
    public static final String GET_USER_BY_ID = "/admin/users/{id}";
    public static final String GET_USER_ORDER = "/admin/users/orders";

    public static final String PRODUCT_CREATE = "/admin/products/create";
    public static final String PRODUCT_UPDATED = "/admin/products/updated/{id}";
    public static final String PRODUCT_DELETE = "/admin/products/delete/{id}";

    public static final String VARIANT_CREATE = "/admin/variants/create";
    public static final String VARIANT_UPDATED = "/admin/variants/updated/{id}";
    public static final String VARIANT_DELETE = "/admin/variants/delete/{id}";

    public static final String CATEGORY_CREATE = "/admin/categories/create";
    public static final String CATEGORY_UPDATED = "/admin/categories/updated/{id}";
    public static final String CATEGORY_DELETE = "/admin/categories/delete/{id}";

    public static final String BRAND_CREATE = "/admin/brands/create";
    public static final String BRAND_UPDATED = "/admin/brands/updated/{id}";
    public static final String BRAND_DELETE = "/admin/brands/delete/{id}";

    public static final String INVENTORY_IMPORT = "/admin/inventories/import";
    public static final String INVENTORY_EXPORT = "/admin/inventories/export";
    public static final String INVENTORY_GET_ALL = "/admin/inventories";

    public static final String FILE_UPLOAD_PRODUCT = "/admin/upload/products/{productId}";
    public static final String FILE_UPLOAD_VARIANT = "/admin/upload/variants/{variantId}";
    public static final String FILE_UPLOAD_CATEGORY = "/admin/upload/categories/{id}";
    public static final String FILE_UPLOAD_BRAND = "/admin/upload/brands/{id}";

    // Customer
    public static final String CUSTOMER_INFO = "/customer";
    public static final String CUSTOMER_UPDATE = "/customer/updated";
    public static final String RESET_PASSWORD = "/customer/reset-password";

    public static final String ADDRESS_CREATE = "/address";
    public static final String ADDRESS_GET_ALL = "/address";
    public static final String ADDRESS_ACTIVATE = "/address/active/{addressId}";
    public static final String ADDRESS_UPDATE = "/address/updated";
    public static final String ADDRESS_DELETE = "/address/delete/{addressId}";

    public static final String FILE_UPLOAD_AVATAR = "/upload/avatar";

    public static final String PRODUCT_GET_ALL = "/public/products";
    public static final String PRODUCT_GET_ID = "/public/products/{id}";

    public static final String CATEGORY_GET_ALL = "/public/categories";
    public static final String CATEGORY_GET_ID = "/public/categories/{id}";

    public static final String BRAND_GET_ALL = "/public/brands";
    public static final String BRAND_GET_ID = "/public/brands/{id}";

}
