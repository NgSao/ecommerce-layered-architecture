package com.nguyensao.ecommerce_layered_architecture.event.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.RabbitMqConstant;
import com.nguyensao.ecommerce_layered_architecture.event.domain.ProductEvent;
import com.nguyensao.ecommerce_layered_architecture.service.ProductService;

@Component
public class ProductEventListener {
    private final ProductService productService;

    public ProductEventListener(ProductService productService) {
        this.productService = productService;
    }

    @RabbitListener(queues = RabbitMqConstant.PRODUCT_QUEUE)
    public void handleProductEvent(ProductEvent event) {
        try {

            switch (event.getEventType()) {
                case PRODUCT_RATING:
                    productService.updatedRating(event);
                    break;

                default:
                    System.err.println("Product event type: " + event.getEventType());
            }
        } catch (Exception e) {
            System.err.println("Error processing event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
