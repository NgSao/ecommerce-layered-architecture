package com.nguyensao.ecommerce_layered_architecture.controller;

import com.nguyensao.ecommerce_layered_architecture.dto.request.ConversationRequest;
import com.nguyensao.ecommerce_layered_architecture.messages.Conversation;
import com.nguyensao.ecommerce_layered_architecture.messages.Message;
import com.nguyensao.ecommerce_layered_architecture.service.ChatService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/public/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat/{conversationId}")
    @SendTo("/topic/conversation/{conversationId}")
    public Message sendMessageAdmin(@DestinationVariable String conversationId, Message message) {
        return chatService.sendMessage(conversationId, message);
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> getAllConversations() {
        return ResponseEntity.ok(chatService.getAllConversations());
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<?> getConversationById(@PathVariable String id) {
        return ResponseEntity.ok(chatService.getConversationById(id));
    }

    @GetMapping("/conversations/user/{userId}")
    public ResponseEntity<?> getConversationByUserId(@PathVariable String userId) {
        Conversation conversation = chatService.getConversationByUserId(userId);

        if (conversation.getUnreadCount() != 0) {
            return ResponseEntity.ok(conversation);
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
        }
    }

    @PostMapping("/conversations/messages/{id}")
    public ResponseEntity<?> sendMessage(@PathVariable String id, @RequestBody Message message) {
        return ResponseEntity.ok(chatService.sendMessage(id, message));
    }

    @PostMapping("/conversations/read/{id}")
    public ResponseEntity<?> markConversationAsRead(@PathVariable String id, @RequestParam String userId) {
        chatService.markConversationAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/conversations")
    public ResponseEntity<?> createConversation(@RequestBody ConversationRequest request) {
        return ResponseEntity.ok(chatService.createConversation(request));
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<?> getTotalUnreadMessages(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.getTotalUnreadMessages(userId));
    }

    // @PostMapping(value = "/upload-image/{conversationId}/{messageId}", consumes =
    // "multipart/form-data")
    // public ResponseEntity<String> uploadImage(
    // @PathVariable String conversationId,
    // @PathVariable String messageId,
    // @RequestParam("file") MultipartFile image) throws IOException {
    // return ResponseEntity.ok().body(chatService.uploadImage(conversationId,
    // messageId, image));
    // }

    @PostMapping(value = "/upload-image", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile image)
            throws IOException {
        String imageUrl = chatService.uploadImage(image);
        Map<String, String> response = new HashMap<>();
        response.put("data", imageUrl);
        return ResponseEntity.ok(response);
    }

}