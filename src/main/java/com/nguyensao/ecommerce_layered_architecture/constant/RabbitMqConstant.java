package com.nguyensao.ecommerce_layered_architecture.constant;

public class RabbitMqConstant {
    public static final String INVENTORY_EXCHANGE = "inventory.exchange";
    public static final String INVENTORY_QUEUE = "inventory.queue";
    public static final String INVENTORY_ROUTING_KEY = "inventory.event";

    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.event";

    public static final String PRODUCT_EXCHANGE = "product.exchange";
    public static final String PRODUCT_QUEUE = "product.queue";
    public static final String PRODUCT_ROUTING_KEY = "product.event";

    public static final String FILE_EXCHANGE = "file.exchange";
    public static final String FILE_QUEUE = "file.queue";
    public static final String FILE_ROUTING_KEY = "file.event";

}
