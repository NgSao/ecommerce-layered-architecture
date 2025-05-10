package com.nguyensao.ecommerce_layered_architecture.messages;

import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Conversation {
    private String id;
    private String userId;
    private String userName;
    private String userAvatar;
    private String lastMessage;
    private Instant lastMessageTime;
    private int unreadCount;
    private List<Message> messages = new ArrayList<>();
}