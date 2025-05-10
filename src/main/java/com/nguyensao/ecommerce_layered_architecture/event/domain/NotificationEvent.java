package com.nguyensao.ecommerce_layered_architecture.event.domain;

import com.nguyensao.ecommerce_layered_architecture.event.EventType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent {
    EventType eventType;
    String notification;

}
