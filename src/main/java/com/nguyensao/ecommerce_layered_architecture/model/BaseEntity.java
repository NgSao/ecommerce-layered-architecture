package com.nguyensao.ecommerce_layered_architecture.model;

import java.time.Instant;

import com.nguyensao.ecommerce_layered_architecture.utils.JwtUtil;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BaseEntity {
    Instant createdAt;

    Instant updatedAt;

    String createdBy;

    String updatedBy;

    @PrePersist
    public void beforeCreate() {
        this.createdBy = JwtUtil.getCurrentUserLogin().isPresent() == true
                ? JwtUtil.getCurrentUserLogin().get()
                : "";
        createdAt = Instant.now();
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedBy = JwtUtil.getCurrentUserLogin().isPresent() == true
                ? JwtUtil.getCurrentUserLogin().get()
                : "";
        updatedAt = Instant.now();
    }

}
