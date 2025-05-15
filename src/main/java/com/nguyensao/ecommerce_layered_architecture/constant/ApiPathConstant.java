package com.nguyensao.ecommerce_layered_architecture.constant;

public class ApiPathConstant {
    public static final String API_PREFIX = "/api/v1";

    public static final String CHAT_WEBSOCKET_ENDPOINT = API_PREFIX + "/public/chat-websocket";
    public static final String NOTIFICATION_WEBSOCKET_ENDPOINT = API_PREFIX + "/public/notification-websocket";

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

    public static final String ADMIN_STATS = "/admin/stats";

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
    public static final String PRODUCT_GET = "/admin/products/{id}";

    public static final String VARIANT_CREATE = "/admin/variants/create";
    public static final String VARIANT_UPDATED = "/admin/variants/updated/{id}";
    public static final String VARIANT_DELETE = "/admin/variants/delete/{id}";

    public static final String CATEGORY_GET = "/admin/categories";
    public static final String CATEGORY_CREATE = "/admin/categories/create";
    public static final String CATEGORY_UPDATED = "/admin/categories/updated/{id}";
    public static final String CATEGORY_DELETE = "/admin/categories/delete/{id}";

    public static final String BRAND_CREATE = "/admin/brands/create";
    public static final String BRAND_GET = "/admin/brands";
    public static final String BRAND_UPDATED = "/admin/brands/updated/{id}";
    public static final String BRAND_DELETE = "/admin/brands/delete/{id}";

    public static final String INVENTORY_IMPORT = "/admin/inventories/import";
    public static final String INVENTORY_EXPORT = "/admin/inventories/export";
    public static final String INVENTORY_GET_ALL = "/admin/inventories";

    public static final String FILE_UPLOAD_PRODUCT = "/admin/upload/products/{productId}";
    public static final String FILE_UPLOAD_VARIANT = "/admin/upload/variants/{variantId}";
    public static final String FILE_UPLOAD_CATEGORY = "/admin/upload/categories/{id}";
    public static final String FILE_UPLOAD_BRAND = "/admin/upload/brands/{id}";
    public static final String FILE_UPLOAD_BANNER = "/admin/upload/banners/{id}";

    public static final String BANNER_CREATE = "/admin/banners/create";
    public static final String BANNER_UPDATED = "/admin/banners/updated/{id}";
    public static final String BANNER_DELETE = "/admin/banners/delete/{id}";

    // Customer + Token
    public static final String CUSTOMER_INFO = "/customer";
    public static final String CUSTOMER_UPDATE = "/customer/updated";
    public static final String RESET_PASSWORD = "/customer/reset-password";

    public static final String ADDRESS_CREATE = "/address";
    public static final String ADDRESS_GET_ALL = "/address";
    public static final String ADDRESS_ACTIVATE = "/address/active/{addressId}";
    public static final String ADDRESS_UPDATE = "/address/updated";
    public static final String ADDRESS_DELETE = "/address/delete/{addressId}";

    public static final String FILE_UPLOAD_AVATAR = "/upload/avatar";

    // Public
    public static final String PRODUCT_GET_ALL = "/public/products";
    public static final String PRODUCT_GET_ID = "/public/products/{id}";
    public static final String PRODUCT_GET_HOT = "/public/products/hot";
    public static final String PRODUCT_GET_SALE = "/public/products/sale";
    public static final String PRODUCT_GET_COLOR = "/public/products/colors";
    public static final String PRODUCT_GET_SEARCH = "/public/products/search";
    public static final String PRODUCT_GET_CATEGORY = "/public/products/categories/{id}";

    public static final String CATEGORY_GET_ALL = "/public/categories";
    public static final String CATEGORY_GET_ID = "/public/categories/{id}";

    public static final String BRAND_GET_ALL = "/public/brands";
    public static final String BRAND_GET_ID = "/public/brands/{id}";

    public static final String BANNER_GET_ALL = "/public/banners";
    public static final String BANNER_GET_ID = "/public/banners/{id}";

    // Chat
    public static final String CHAT_ENDPOINT = API_PREFIX + "/public/chat";
    public static final String CHAT_CONVERSATIONS = "/conversations";
    public static final String CHAT_CONVERSATION_BY_ID = "/conversations/{id}";
    public static final String CHAT_CONVERSATION_BY_USER = "/conversations/user/{userId}";
    public static final String CHAT_SEND_MESSAGE = "/conversations/messages/{id}";
    public static final String CHAT_MARK_READ = "/conversations/read/{id}";
    public static final String CHAT_CREATE_CONVERSATION = "/conversations";
    public static final String CHAT_TOTAL_UNREAD = "/unread/{userId}";
    public static final String CHAT_UPLOAD_IMAGE = "/upload-image";

    // Notification
    public static final String MY_NOTIFICATIONS = "/public/notifications/my-notifications";
    public static final String NOTIFICATIONS_BY_USER = "/public/notifications/user/{userId}";
    public static final String UNREAD_BY_USER = "/public/notifications/user/{userId}/unread";
    public static final String READ_NOTIFICATION = "/public/notifications/read/{id}";
    public static final String READ_ALL_NOTIFICATIONS = "/public/notifications/read-all";
    public static final String DELETE_NOTIFICATION = "/public/notifications/delete/{id}";

    public static final String NOTIFICATION_ADMIN = "/admin/notifications";

    // Order
    public static final String CREATE_ORDER = "/user/create-order";
    public static final String MY_ORDERS = "/user/my-orders";
    public static final String USER_ORDERS_BY_ID = "/user/{userId}";
    public static final String CANCEL_ORDER = "/user/orders/cancel/{id}";
    public static final String GET_ORDER_BY_ID = "/user/orders/{id}";

    public static final String UPDATE_ORDER_STATUS = "/admin/orders/status/{id}";
    public static final String ORDER_GET = "/admin/orders";

    public static final String ORDER_STATUS_STATS = "/admin/orders/status-stats";
    public static final String REVENUE_STATS = "/admin/orders/revenue-stats";

    // Payment
    // Momo
    public static final String MOMO_CREATE = "/public/momo/payment";
    public static final String MOMO_CALLBACK = "/public/momo/callback";

}
