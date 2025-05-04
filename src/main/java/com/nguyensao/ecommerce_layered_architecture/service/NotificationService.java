package com.nguyensao.ecommerce_layered_architecture.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final RedisTemplate<String, String> redisTemplate;

    public NotificationService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
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
}