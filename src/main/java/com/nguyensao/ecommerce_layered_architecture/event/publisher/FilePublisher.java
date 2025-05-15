package com.nguyensao.ecommerce_layered_architecture.event.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.RabbitMqConstant;
import com.nguyensao.ecommerce_layered_architecture.event.domain.FileEvent;

@Component
public class FilePublisher {

    private final RabbitTemplate rabbitTemplate;

    public FilePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendFile(FileEvent event) {
        rabbitTemplate.convertAndSend(RabbitMqConstant.FILE_EXCHANGE, RabbitMqConstant.FILE_ROUTING_KEY, event);
    }
}
