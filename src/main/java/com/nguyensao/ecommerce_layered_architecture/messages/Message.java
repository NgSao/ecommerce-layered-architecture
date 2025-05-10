package com.nguyensao.ecommerce_layered_architecture.messages;

import lombok.*;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message implements Serializable {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private Instant timestamp;
    private boolean read;
    private String image;
}