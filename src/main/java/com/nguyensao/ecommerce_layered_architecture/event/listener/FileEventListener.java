package com.nguyensao.ecommerce_layered_architecture.event.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.RabbitMqConstant;
import com.nguyensao.ecommerce_layered_architecture.event.domain.FileEvent;
import com.nguyensao.ecommerce_layered_architecture.service.ReviewService;

@Component
public class FileEventListener {
    private final ReviewService reviewService;

    public FileEventListener(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @RabbitListener(queues = RabbitMqConstant.FILE_QUEUE)
    public void handleFileEvent(FileEvent event) {
        try {

            switch (event.getEventType()) {
                case FILE_REVIEWS:
                    reviewService.uploadImageReview(event);
                    break;

                default:
                    System.err.println("File event type: " + event.getEventType());
            }
        } catch (Exception e) {
            System.err.println("Error processing event: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
