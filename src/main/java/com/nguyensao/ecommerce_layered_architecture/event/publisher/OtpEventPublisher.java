package com.nguyensao.ecommerce_layered_architecture.event.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.KafkaConstant;
import com.nguyensao.ecommerce_layered_architecture.event.domain.OtpEvent;

@Component
public class OtpEventPublisher {
    private final KafkaTemplate<String, OtpEvent> kafkaTemplate;

    public OtpEventPublisher(KafkaTemplate<String, OtpEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void publishOtpEvent(OtpEvent event) {
        kafkaTemplate.send(KafkaConstant.KAFKA_OTP_EVENT, event);
    }
}
