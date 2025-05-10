package com.nguyensao.ecommerce_layered_architecture.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

import com.nguyensao.ecommerce_layered_architecture.enums.NotificationEnum;

@Data
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private NotificationEnum type;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Instant date;

    @Column(name = "`read`")
    private boolean read;

    @Column(columnDefinition = "TEXT")
    private String data;
}
