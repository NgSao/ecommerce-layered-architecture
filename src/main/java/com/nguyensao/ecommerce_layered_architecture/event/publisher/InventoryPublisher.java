package com.nguyensao.ecommerce_layered_architecture.event.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.RabbitMqConstant;
import com.nguyensao.ecommerce_layered_architecture.event.EventType;
import com.nguyensao.ecommerce_layered_architecture.event.domain.InventoryEvent;

@Component
public class InventoryPublisher {

    private final RabbitTemplate rabbitTemplate;

    public InventoryPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    private void publishEvent(EventType eventType, String skuProduct, String skuVariant, Integer quantity) {
        InventoryEvent inventoryEvent = new InventoryEvent();
        inventoryEvent.setEventType(eventType);
        inventoryEvent.setSkuProduct(skuProduct);
        inventoryEvent.setSkuVariant(skuVariant);
        inventoryEvent.setQuantity(quantity);
        rabbitTemplate.convertAndSend(RabbitMqConstant.INVENTORY_EXCHANGE, RabbitMqConstant.INVENTORY_ROUTING_KEY,
                inventoryEvent);
    }

    public void publishInventoryEvent(EventType eventType, String skuProduct, String skuVariant, Integer quantity) {
        publishEvent(eventType, skuProduct, skuVariant, quantity);
    }

    public void publishProductEvent(EventType eventType, String skuProduct, String skuVariant, Integer quantity) {
        publishEvent(eventType, skuProduct, skuVariant, quantity);
    }
}