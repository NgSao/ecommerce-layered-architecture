package com.nguyensao.ecommerce_layered_architecture.event.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.event.domain.InventoryEvent;
import com.nguyensao.ecommerce_layered_architecture.service.InventoryService;
import com.nguyensao.ecommerce_layered_architecture.service.NotificationService;

@Component
public class InventoryEventListener {
    private final InventoryService inventoryService;
    private final NotificationService notificationService;

    public InventoryEventListener(InventoryService inventoryService, NotificationService notificationService) {
        this.inventoryService = inventoryService;
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "inventory.queue")
    public void handleInventoryEvent(InventoryEvent event) {
        try {
            String eventData = String.format("SKU Product: %s, SKU Variant: %s, Quantity: %d",
                    event.getSkuProduct(), event.getSkuVariant(), event.getQuantity());

            notificationService.saveEvent("InventoryEvent", eventData);
            switch (event.getEventType()) {
                case CREATE_INVENTORY:
                    inventoryService.createInventory(event);
                    break;
                case UPDATE_INVENTORY:
                    inventoryService.updateInventory(event);
                    break;
                case DELETE_INVENTORY:
                    inventoryService.deleteInventory(event);
                    break;
                default:
                    System.err.println("Invalid event type: " + event.getEventType());
            }
        } catch (Exception e) {
            System.err.println("Error processing event: " + e.getMessage());
        }
    }
}
