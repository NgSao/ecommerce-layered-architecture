package com.nguyensao.ecommerce_layered_architecture.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.dto.request.ConversationRequest;
import com.nguyensao.ecommerce_layered_architecture.messages.Conversation;
import com.nguyensao.ecommerce_layered_architecture.messages.Message;
import com.nguyensao.ecommerce_layered_architecture.messages.RedisChatService;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final RedisChatService redisChatService;
    private final FileService fileService;

    public ChatService(RedisChatService redisChatService, FileService fileService) {
        this.redisChatService = redisChatService;
        this.fileService = fileService;
    }

    public List<Conversation> getAllConversations() {
        return redisChatService.getAllConversations();
    }

    public Conversation getConversationById(String conversationId) {
        Conversation conversation = redisChatService.getConversationById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation not found");
        }
        return conversation;
    }

    public Conversation getConversationByUserId(String userId) {
        Conversation conversation = redisChatService.getConversationByUserId(userId);

        if (conversation == null) {
            conversation = Conversation.builder()
                    .id("conv_" + UUID.randomUUID())
                    .userId(userId)
                    .userName("Khách hàng mới")
                    .lastMessageTime(Instant.now())
                    .unreadCount(0)
                    .build();
        }

        return conversation;
    }

    public Message sendMessage(String conversationId, Message message) {
        Conversation conversation = getConversationById(conversationId);
        message.setId("msg_" + UUID.randomUUID());
        message.setTimestamp(Instant.now());

        redisChatService.saveMessage(conversationId, message);

        conversation.setLastMessage(message.getContent());
        conversation.setLastMessageTime(message.getTimestamp());
        conversation.setUnreadCount(message.getSenderId().equals("ADMIN") ? conversation.getUnreadCount() + 1 : 0);
        conversation.getMessages().add(message);
        redisChatService.saveConversation(conversation);

        return message;
    }

    public void markConversationAsRead(String conversationId, String userId) {
        Conversation conversation = getConversationById(conversationId);
        conversation.getMessages().forEach(msg -> {
            if (msg.getReceiverId().equals(userId)) {
                msg.setRead(true);
            }
        });
        conversation.setUnreadCount(0);
        redisChatService.saveConversation(conversation);
    }

    public Conversation createConversation(ConversationRequest request) {
        Conversation existing = redisChatService.getConversationByUserId(request.getUserId());
        if (existing != null) {
            return existing;
        }
        Conversation conversation = Conversation.builder()
                .id("conv_" + UUID.randomUUID())
                .userId(request.getUserId())
                .userName(request.getUserName())
                .userAvatar(request.getUserAvatar())
                .lastMessageTime(Instant.now())
                .unreadCount(0)
                .build();
        redisChatService.saveConversation(conversation);
        return conversation;
    }

    public int getTotalUnreadMessages(String userId) {
        List<Conversation> conversations = userId.equals("ADMIN") ? redisChatService.getAllConversations()
                : List.of(redisChatService.getConversationByUserId(userId));
        return conversations.stream()
                .flatMap(conv -> conv.getMessages().stream())
                .filter(msg -> msg.getReceiverId().equals(userId) && !msg.isRead())
                .mapToInt(msg -> 1)
                .sum();
    }

    public String uploadImage(MultipartFile image) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("Image is required for upload");
        }
        String imageUrl = fileService.uploadImage(image);
        return imageUrl;
    }

}