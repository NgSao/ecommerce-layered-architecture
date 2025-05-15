package com.nguyensao.ecommerce_layered_architecture.event.domain;

import com.nguyensao.ecommerce_layered_architecture.dto.OrderDto;
import com.nguyensao.ecommerce_layered_architecture.enums.OrderStatus;
import com.nguyensao.ecommerce_layered_architecture.event.EventType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderEvent {
    EventType eventType;
    String email;
    String orderCode;
    OrderStatus orderStatus;
    OrderDto orderDto;
    Boolean flag;

}
