package com.nguyensao.ecommerce_layered_architecture.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyensao.ecommerce_layered_architecture.dto.NotificationDto;
import com.nguyensao.ecommerce_layered_architecture.model.Notification;
import com.nguyensao.ecommerce_layered_architecture.service.NotificationService;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/public/notifications/{userId}")
    public List<String> getNotifications(@PathVariable String userId) {
        return notificationService.getNotifications(userId);
    }

    @GetMapping("/public/notifications")
    public List<String> getAllNotificationss() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/admin/notifications/event")
    public ResponseEntity<List<String>> getAllNotificationsEvent() {
        return ResponseEntity.ok().body(notificationService.getAllEvents());
    }

    // API cho người dùng
    @GetMapping("/public/notifications/my-notifications")
    public ResponseEntity<List<Notification>> getNotificationsByToken() {
        List<Notification> notifications = notificationService.getNotificationsByToken();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/public/notifications/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/public/notifications/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUserId(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/public/notifications/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    // API cho admin
    @PostMapping("/admin/notifications")
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationDto notificationDTO) {
        Notification notification = notificationService.createNotification(notificationDTO);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/admin/notifications")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotificationsAll();
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/admin/notifications/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}