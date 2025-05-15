package com.nguyensao.ecommerce_layered_architecture.event.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.KafkaConstant;
import com.nguyensao.ecommerce_layered_architecture.event.domain.OrderEvent;

@Component
public class OrderEventPublisher {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void publishOrderEvent(OrderEvent event) {
        kafkaTemplate.send(KafkaConstant.KAFKA_ORDER_EVENT, event);
    }
}
