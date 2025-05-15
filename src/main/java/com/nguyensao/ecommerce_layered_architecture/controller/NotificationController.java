package com.nguyensao.ecommerce_layered_architecture.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.NotificationDto;
import com.nguyensao.ecommerce_layered_architecture.model.Notification;
import com.nguyensao.ecommerce_layered_architecture.service.NotificationService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // API cho người dùng
    @GetMapping(ApiPathConstant.MY_NOTIFICATIONS)
    public ResponseEntity<List<Notification>> getNotificationsByToken() {
        return ResponseEntity.ok().body(notificationService.getNotificationsByToken());
    }

    @GetMapping(ApiPathConstant.NOTIFICATIONS_BY_USER)
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping(ApiPathConstant.UNREAD_BY_USER)
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUserId(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping(ApiPathConstant.READ_NOTIFICATION)
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @GetMapping(ApiPathConstant.READ_ALL_NOTIFICATIONS)
    public ResponseEntity<?> markAllAsRead() {
        notificationService.markAllAsReadForCurrentUser();
        return ResponseEntity.ok("All notifications marked as read.");
    }

    @DeleteMapping(ApiPathConstant.DELETE_NOTIFICATION)
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    // API cho admin
    @PostMapping(ApiPathConstant.NOTIFICATION_ADMIN)
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationDto notificationDTO) {
        Notification notification = notificationService.createNotification(notificationDTO);
        return ResponseEntity.ok(notification);
    }

    @GetMapping(ApiPathConstant.NOTIFICATION_ADMIN)
    public ResponseEntity<List<Notification>> getAllNotifications(
            @RequestParam(defaultValue = "10") int limit) {
        List<Notification> notifications = notificationService.getLatestNotifications(limit);
        return ResponseEntity.ok(notifications);
    }

}