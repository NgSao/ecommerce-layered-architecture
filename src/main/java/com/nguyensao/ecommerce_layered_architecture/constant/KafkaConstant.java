package com.nguyensao.ecommerce_layered_architecture.constant;

public class KafkaConstant {

    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static final String OTP_GROUP_ID = "otp-group";
    public static final String ORDER_GROUP_ID = "order-group";

    // Xử lý các message củ
    public static final String AUTO_OFFSET_RESET_EARLIEST = "earliest";

    // Xử lý các message mới
    public static final String AUTO_OFFSET_RESET_LATEST = "latest";

    public static final String TRUSTED_PACKAGE = "com.nguyensao.ecommerce_layered_architecture.event.domain";

    public static final String KAFKA_OTP_EVENT = "otp-events";
    public static final String KAFKA_ORDER_EVENT = "order-events";

}
