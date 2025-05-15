package com.nguyensao.ecommerce_layered_architecture.event.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.KafkaConstant;
import com.nguyensao.ecommerce_layered_architecture.event.domain.OtpEvent;
import com.nguyensao.ecommerce_layered_architecture.service.EmailService;

@Component
public class OtpEventListener {
    private final EmailService emailService;

    public OtpEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = KafkaConstant.KAFKA_OTP_EVENT, groupId = KafkaConstant.OTP_GROUP_ID, containerFactory = "otpKafkaListenerContainerFactory")
    public void listenOtpEvents(OtpEvent event) {
        switch (event.getEventType()) {
            case REGISTER_OTP:
                emailService.sendVerificationEmail(event.getFullName(), event.getEmail(), event.getOtp());
                break;
            case VERIFY_OTP:
                emailService.sendVerificationEmail(event.getFullName(), event.getEmail(), event.getOtp());
                break;
            case FORGOT_PASSWORD:
                emailService.sendVerificationPassword(event.getFullName(), event.getEmail(), event.getOtp());
                break;
            case RESET_PASSWORD:
                emailService.sendPasswordResetConfirmation(event.getFullName(), event.getEmail());
                break;
            default:
                break;
        }
    }
}
