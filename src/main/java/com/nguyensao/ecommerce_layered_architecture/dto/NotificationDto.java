package com.nguyensao.ecommerce_layered_architecture.dto;

import lombok.Data;

import java.time.Instant;

import com.nguyensao.ecommerce_layered_architecture.enums.NotificationEnum;

@Data
public class NotificationDto {
    private String userId;
    private NotificationEnum type;
    private String title;
    private String message;
    private Instant date;
    private boolean read;
    private String data;
}