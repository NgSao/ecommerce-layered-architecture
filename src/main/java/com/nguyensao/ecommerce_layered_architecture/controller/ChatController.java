package com.nguyensao.ecommerce_layered_architecture.controller;

import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ConversationRequest;
import com.nguyensao.ecommerce_layered_architecture.messages.Conversation;
import com.nguyensao.ecommerce_layered_architecture.messages.Message;
import com.nguyensao.ecommerce_layered_architecture.service.ChatService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(ApiPathConstant.CHAT_ENDPOINT)
public class ChatController {

    private ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat/{conversationId}")
    @SendTo("/topic/conversation/{conversationId}")
    public Message sendMessageAdmin(@DestinationVariable String conversationId, Message message) {
        return chatService.sendMessage(conversationId, message);
    }

    @GetMapping(ApiPathConstant.CHAT_CONVERSATIONS)
    public ResponseEntity<?> getAllConversations() {
        return ResponseEntity.ok().body(chatService.getAllConversations());
    }

    @GetMapping(ApiPathConstant.CHAT_CONVERSATION_BY_ID)
    public ResponseEntity<?> getConversationById(@PathVariable String id) {
        return ResponseEntity.ok().body(chatService.getConversationById(id));
    }

    @GetMapping(ApiPathConstant.CHAT_CONVERSATION_BY_USER)
    public ResponseEntity<?> getConversationByUserId(@PathVariable String userId) {
        Conversation conversation = chatService.getConversationByUserId(userId);
        if (conversation.getUnreadCount() != 0) {
            return ResponseEntity.ok(conversation);
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
        }
    }

    @PostMapping(ApiPathConstant.CHAT_SEND_MESSAGE)
    public ResponseEntity<?> sendMessage(@PathVariable String id, @RequestBody Message message) {
        return ResponseEntity.ok().body(chatService.sendMessage(id, message));
    }

    @PostMapping(ApiPathConstant.CHAT_MARK_READ)
    public ResponseEntity<?> markConversationAsRead(@PathVariable String id, @RequestParam String userId) {
        chatService.markConversationAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiPathConstant.CHAT_CREATE_CONVERSATION)
    public ResponseEntity<?> createConversation(@RequestBody ConversationRequest request) {
        return ResponseEntity.ok().body(chatService.createConversation(request));
    }

    @GetMapping(ApiPathConstant.CHAT_TOTAL_UNREAD)
    public ResponseEntity<?> getTotalUnreadMessages(@PathVariable String userId) {
        return ResponseEntity.ok().body(chatService.getTotalUnreadMessages(userId));
    }

    @PostMapping(value = ApiPathConstant.CHAT_UPLOAD_IMAGE, consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile image)
            throws IOException {
        String imageUrl = chatService.uploadImage(image);
        Map<String, String> response = new HashMap<>();
        response.put("data", imageUrl);
        return ResponseEntity.ok(response);
    }

}