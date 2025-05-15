package com.nguyensao.ecommerce_layered_architecture.event.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.RabbitMqConstant;
import com.nguyensao.ecommerce_layered_architecture.event.domain.ProductEvent;

@Component
public class ProductPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ProductPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendProduct(ProductEvent event) {
        rabbitTemplate.convertAndSend(RabbitMqConstant.PRODUCT_EXCHANGE, RabbitMqConstant.PRODUCT_ROUTING_KEY, event);
    }
}
