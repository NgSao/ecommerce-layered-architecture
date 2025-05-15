package com.nguyensao.ecommerce_layered_architecture.event.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.RabbitMqConstant;
import com.nguyensao.ecommerce_layered_architecture.event.domain.NotificationEvent;

@Component
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(NotificationEvent event) {
        rabbitTemplate.convertAndSend(RabbitMqConstant.NOTIFICATION_EXCHANGE, RabbitMqConstant.NOTIFICATION_ROUTING_KEY,
                event);
    }
}
