package com.nguyensao.ecommerce_layered_architecture.model;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reply {
    String userId;
    String fullName;
    String reply;
    boolean isAdminReply;
    LocalDateTime createdAt = LocalDateTime.now();
}