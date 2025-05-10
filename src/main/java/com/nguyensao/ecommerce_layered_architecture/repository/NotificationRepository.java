package com.nguyensao.ecommerce_layered_architecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyensao.ecommerce_layered_architecture.model.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndRead(Long userId, boolean read);
}