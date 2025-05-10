package com.nguyensao.ecommerce_layered_architecture.event.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.event.domain.OtpEvent;
import com.nguyensao.ecommerce_layered_architecture.service.EmailService;
import com.nguyensao.ecommerce_layered_architecture.service.NotificationService;

@Component
public class OtpEventListener {
    private final EmailService emailService;
    private final NotificationService notificationService;

    public OtpEventListener(EmailService emailService, NotificationService notificationService) {
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "otp-events", groupId = "otp-group")
    public void listenOtpEvents(OtpEvent event) {
        String userId = event.getEmail();
        String action = event.getEventType().name();
        String details = "OTP: " + event.getOtp();
        String eventData = String.format("FullName: %s, Email: %s, OTP: %s",
                event.getFullName(), event.getEmail(), event.getOtp());

        notificationService.saveEvent("OtpEvent", eventData);

        switch (event.getEventType()) {
            case REGISTER_OTP:
                emailService.sendVerificationEmail(event.getFullName(), event.getEmail(), event.getOtp());
                notificationService.saveEventCustomer(userId, action, details);
                break;
            case VERIFY_OTP:
                emailService.sendVerificationEmail(event.getFullName(), event.getEmail(), event.getOtp());
                notificationService.saveEventCustomer(userId, action, details);
                break;
            case FORGOT_PASSWORD:
                emailService.sendVerificationPassword(event.getFullName(), event.getEmail(), event.getOtp());
                notificationService.saveEventCustomer(userId, action, details);
                break;
            case RESET_PASSWORD:
                emailService.sendPasswordResetConfirmation(event.getFullName(), event.getEmail());
                notificationService.saveEventCustomer(userId, action, "Password reset confirmation sent");
                break;
            case CREATE_ORDER:
                emailService.sendOrderConfirmation(event.getEmail(), event.getOrderDto());
            default:
                break;
        }
    }
}
