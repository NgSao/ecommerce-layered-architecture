package com.nguyensao.ecommerce_layered_architecture.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyensao.ecommerce_layered_architecture.service.NotificationService;

@RestController
@RequestMapping("/api/v1/public/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{userId}")
    public List<String> getNotifications(@PathVariable String userId) {
        return notificationService.getNotifications(userId);
    }

    @GetMapping
    public List<String> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/event")
    public ResponseEntity<List<String>> getAllNotificationsEvent() {
        return ResponseEntity.ok().body(notificationService.getAllEvents());
    }
}