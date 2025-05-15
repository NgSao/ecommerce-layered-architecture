package com.nguyensao.ecommerce_layered_architecture.event.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.RabbitMqConstant;
import com.nguyensao.ecommerce_layered_architecture.event.domain.InventoryEvent;
import com.nguyensao.ecommerce_layered_architecture.service.InventoryService;
import com.nguyensao.ecommerce_layered_architecture.service.NotificationService;
import com.nguyensao.ecommerce_layered_architecture.service.ProductService;

@Component
public class InventoryEventListener {
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
    private final ProductService productService;

    public InventoryEventListener(InventoryService inventoryService, NotificationService notificationService,
            ProductService productService) {
        this.inventoryService = inventoryService;
        this.notificationService = notificationService;
        this.productService = productService;
    }

    @RabbitListener(queues = RabbitMqConstant.INVENTORY_QUEUE)
    public void handleInventoryEvent(InventoryEvent event) {
        try {

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
                case ORDER_INVENTORY:
                    inventoryService.deductInventory(event);
                    break;
                case PRODUCT_INVENTORY:
                    productService.updateProductInventory(event);
                    break;

                default:
                    System.err.println("Invalid event type: " + event.getEventType());
            }
        } catch (Exception e) {
            System.err.println("Error processing event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
