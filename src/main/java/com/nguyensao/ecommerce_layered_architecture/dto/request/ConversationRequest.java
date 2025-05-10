package com.nguyensao.ecommerce_layered_architecture.dto.request;

import lombok.Data;

@Data
public class ConversationRequest {
    private String userId;
    private String userName;
    private String userAvatar;
}
