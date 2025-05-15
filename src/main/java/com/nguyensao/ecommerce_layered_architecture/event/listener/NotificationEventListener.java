package com.nguyensao.ecommerce_layered_architecture.event.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.RabbitMqConstant;
import com.nguyensao.ecommerce_layered_architecture.event.domain.NotificationEvent;
import com.nguyensao.ecommerce_layered_architecture.service.NotificationService;

@Component
public class NotificationEventListener {
    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMqConstant.NOTIFICATION_QUEUE)
    public void handleNotificationEvent(NotificationEvent event) {
        try {

            switch (event.getEventType()) {
                case ORDER_NOTIFICATION:
                    notificationService.createOrderNotification(event);
                    break;
                case USER_NOTIFICATION:
                    notificationService.createCustomerNotification(event);
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
