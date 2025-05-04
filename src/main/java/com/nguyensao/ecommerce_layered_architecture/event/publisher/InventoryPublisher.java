package com.nguyensao.ecommerce_layered_architecture.event.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.event.EventType;
import com.nguyensao.ecommerce_layered_architecture.event.domain.InventoryEvent;

@Component
public class InventoryPublisher {

    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE = "inventory.exchange";
    private static final String ROUTING_KEY = "inventory.event";

    public InventoryPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishInventoryEvent(EventType eventType, String skuProduct, String skuVariant, Integer quantity) {
        InventoryEvent inventoryEvent = new InventoryEvent();
        inventoryEvent.setEventType(eventType);
        inventoryEvent.setSkuProduct(skuProduct);
        inventoryEvent.setSkuVariant(skuVariant);
        inventoryEvent.setQuantity(quantity);
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, inventoryEvent);
    }
}