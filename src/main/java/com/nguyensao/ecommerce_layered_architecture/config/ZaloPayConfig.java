package com.nguyensao.ecommerce_layered_architecture.config;

import org.springframework.context.annotation.Configuration;

import com.nguyensao.ecommerce_layered_architecture.constant.PaymentGatewayConstant;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ZaloPayConfig {
    public static final Map<String, String> config = new HashMap<String, String>() {
        {
            put("app_id", PaymentGatewayConstant.ZALOPAY_APP_ID);
            put("key1", PaymentGatewayConstant.ZALOPAY_KEY1);
            put("key2", PaymentGatewayConstant.ZALOPAY_KEY2);
            put("endpoint", PaymentGatewayConstant.ZALOPAY_ENDPOINT);
            put("orderstatus", PaymentGatewayConstant.ZALOPAY_ORDERSTATUS);
            put("callback_url", PaymentGatewayConstant.ZALOPAY_CALLBACK_URL);
        }
    };
}