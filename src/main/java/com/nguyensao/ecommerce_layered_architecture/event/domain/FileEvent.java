package com.nguyensao.ecommerce_layered_architecture.event.domain;

import java.util.List;

import com.nguyensao.ecommerce_layered_architecture.event.EventType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileEvent {
    EventType eventType;
    Long productId;
    String userId;
    String flagData;
    List<String> imageUrls;;
}
