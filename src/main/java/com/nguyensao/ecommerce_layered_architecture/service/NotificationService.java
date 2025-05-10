package com.nguyensao.ecommerce_layered_architecture.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.nguyensao.ecommerce_layered_architecture.dto.NotificationDto;
import com.nguyensao.ecommerce_layered_architecture.enums.NotificationEnum;
import com.nguyensao.ecommerce_layered_architecture.model.Notification;
import com.nguyensao.ecommerce_layered_architecture.repository.NotificationRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final RedisTemplate<String, String> redisTemplate;
    private final NotificationRepository notificationRepository;

    public NotificationService(RedisTemplate<String, String> redisTemplate,
            NotificationRepository notificationRepository) {
        this.redisTemplate = redisTemplate;
        this.notificationRepository = notificationRepository;
    }

    public void saveEventCustomer(String userId, String action, String details) {
        String key = "notification:" + userId + ":" + System.currentTimeMillis();
        String value = String.format("User: %s | Action: %s | Details: %s | Time: %s",
                userId, action, details, LocalDateTime.now());
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }

    public List<String> getNotifications(String userId) {
        Set<String> keys = redisTemplate.keys("notification:" + userId + ":*");
        if (keys != null) {
            return keys.stream()
                    .map(key -> redisTemplate.opsForValue().get(key))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<String> getAllNotifications() {
        Set<String> keys = redisTemplate.keys("notification:*:*");
        if (keys != null) {
            return keys.stream()
                    .map(key -> redisTemplate.opsForValue().get(key))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public void saveEvent(String eventType, String eventData) {
        String key = "event:" + eventType + ":" + System.currentTimeMillis();
        String value = String.format("EventType: %s | Data: %s | Time: %s",
                eventType, eventData, LocalDateTime.now());
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, 1, TimeUnit.HOURS); // Expire after 1 hour
    }

    public List<String> getAllEvents() {
        Set<String> keys = redisTemplate.keys("event:*:*");
        if (keys != null) {
            return keys.stream()
                    .map(key -> redisTemplate.opsForValue().get(key))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public Notification createNotification(NotificationDto notificationDTO) {
        Notification notification = new Notification();
        notification.setUserId(notificationDTO.getUserId());
        notification.setType(notification.getType());
        notification.setTitle(notificationDTO.getTitle());
        notification.setMessage(notificationDTO.getMessage());
        notification.setDate(notificationDTO.getDate() != null ? notificationDTO.getDate() : Instant.now());
        notification.setRead(notificationDTO.isRead());
        notification.setData(notificationDTO.getData());
        return notificationRepository.save(notification);
    }

    public void createOrderNotification(Long userId, String orderCode, String status) {
        NotificationDto notificationDTO = new NotificationDto();
        notificationDTO.setUserId(userId);
        notificationDTO.setType(NotificationEnum.ORDER);
        notificationDTO.setDate(Instant.now());
        notificationDTO.setRead(false);
        notificationDTO.setData("{\"orderCode\": \"" + orderCode + "\"}");

        switch (status) {
            case "CONFIRMED":
                notificationDTO.setTitle("Đơn hàng #" + orderCode + " đã được xác nhận");
                notificationDTO.setMessage("Đơn hàng của bạn đã được xác nhận và đang được chuẩn bị giao hàng.");
                break;
            case "DELIVERED":
                notificationDTO.setTitle("Đơn hàng #" + orderCode + " đã được giao thành công");
                notificationDTO.setMessage(
                        "Đơn hàng của bạn đã được giao thành công. Cảm ơn bạn đã mua sắm tại Minh Tuấn Mobile!");
                break;
            case "CANCELLED":
                notificationDTO.setTitle("Đơn hàng #" + orderCode + " đã bị hủy");
                notificationDTO
                        .setMessage("Đơn hàng của bạn đã bị hủy. Vui lòng liên hệ hỗ trợ nếu cần thêm thông tin.");
                break;
            default:
                return;
        }

        createNotification(notificationDTO);
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdAndRead(userId, false);
    }

    public List<Notification> getNotificationsByToken() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return notificationRepository.findByUserId(Long.parseLong(userId));
    }

    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!notification.getUserId().equals(Long.parseLong(userId))) {
            throw new RuntimeException("You can only mark your own notifications as read");
        }
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotificationsAll() {
        return notificationRepository.findAll();
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}