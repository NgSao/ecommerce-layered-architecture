package com.nguyensao.ecommerce_layered_architecture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

import com.nguyensao.ecommerce_layered_architecture.enums.NotificationEnum;
import com.nguyensao.ecommerce_layered_architecture.enums.RoleAuthorities;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long flagId;

    private String userId;

    @Enumerated(EnumType.STRING)
    private NotificationEnum type;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Instant date;

    @Column(name = "`read`")
    private boolean read;

    private String data;

    private boolean active;

    private RoleAuthorities role;
}
