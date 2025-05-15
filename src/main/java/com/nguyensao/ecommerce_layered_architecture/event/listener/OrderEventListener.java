package com.nguyensao.ecommerce_layered_architecture.event.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.KafkaConstant;
import com.nguyensao.ecommerce_layered_architecture.event.domain.OrderEvent;
import com.nguyensao.ecommerce_layered_architecture.service.EmailService;

@Component
public class OrderEventListener {
    private final EmailService emailService;

    public OrderEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = KafkaConstant.KAFKA_ORDER_EVENT, groupId = KafkaConstant.ORDER_GROUP_ID, containerFactory = "orderKafkaListenerContainerFactory")
    public void listenOrderEvents(OrderEvent event) {
        switch (event.getEventType()) {
            case KAFKA_ORDER:
                switch (event.getOrderStatus()) {
                    case SHIPPED:
                    case DELIVERED:
                        emailService.sendShippingUpdate(
                                event.getEmail(),
                                event.getOrderStatus(),
                                event.getOrderDto());
                        break;
                    default:
                        emailService.sendOrderConfirmation(
                                event.getEmail(),
                                event.getOrderStatus(),
                                event.getOrderDto(),
                                event.getFlag());
                        break;
                }
                break;
            default:
                break;
        }
    }
}
