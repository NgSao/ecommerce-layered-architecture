package com.nguyensao.ecommerce_layered_architecture.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RedisChatService {

    private static final String CONVERSATION_KEY_PREFIX = "chat:conversation:";
    private static final String MESSAGE_KEY_PREFIX = "chat:messages:";
    private static final String USER_CONVERSATION_KEY = "chat:user:conversation:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveConversation(Conversation conversation) {
        String key = CONVERSATION_KEY_PREFIX + conversation.getId();
        Map<String, Object> data = new HashMap<>();
        data.put("id", conversation.getId());
        data.put("userId", conversation.getUserId());
        data.put("userName", conversation.getUserName());
        data.put("userAvatar", conversation.getUserAvatar());
        data.put("lastMessage", conversation.getLastMessage() != null ? conversation.getLastMessage() : "");
        data.put("lastMessageTime", conversation.getLastMessageTime().toString());
        data.put("unreadCount", String.valueOf(conversation.getUnreadCount()));
        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.expire(key, Duration.ofMinutes(60)); // ⏱️ TTL 60 phút

        String userSetKey = USER_CONVERSATION_KEY + conversation.getUserId();
        redisTemplate.opsForSet().add(userSetKey, conversation.getId());
        redisTemplate.expire(userSetKey, Duration.ofMinutes(60)); //
    }

    public Conversation getConversationById(String conversationId) {
        String key = CONVERSATION_KEY_PREFIX + conversationId;
        Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
        if (data.isEmpty()) {
            return null;
        }
        Conversation conversation = Conversation.builder()
                .id((String) data.get("id"))
                .userId((String) data.get("userId"))
                .userName((String) data.get("userName"))
                .userAvatar((String) data.get("userAvatar"))
                .lastMessage((String) data.get("lastMessage"))
                .lastMessageTime(Instant.parse((String) data.get("lastMessageTime")))
                .unreadCount(Integer.parseInt((String) data.get("unreadCount")))
                .messages(getMessages(conversationId))
                .build();
        return conversation;
    }

    public Conversation getConversationByUserId(String userId) {
        Set<Object> conversationIds = redisTemplate.opsForSet().members(USER_CONVERSATION_KEY + userId);
        if (conversationIds == null || conversationIds.isEmpty()) {
            return null;
        }
        String conversationId = (String) conversationIds.iterator().next();
        return getConversationById(conversationId);
    }

    public List<Conversation> getAllConversations() {
        Set<String> keys = redisTemplate.keys(CONVERSATION_KEY_PREFIX + "*");
        if (keys == null) {
            return new ArrayList<>();
        }
        return keys.stream()
                .map(key -> getConversationById(key.substring(CONVERSATION_KEY_PREFIX.length())))
                .filter(Objects::nonNull)
                .sorted((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()))
                .collect(Collectors.toList());
    }

    public void saveMessage(String conversationId, Message message) {
        String key = MESSAGE_KEY_PREFIX + conversationId;
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, Duration.ofMinutes(60)); //
    }

    public List<Message> getMessages(String conversationId) {
        String key = MESSAGE_KEY_PREFIX + conversationId;
        List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);
        return messages != null ? messages.stream()
                .map(msg -> (Message) msg)
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    public void deleteConversation(String conversationId) {
        String convKey = CONVERSATION_KEY_PREFIX + conversationId;
        String msgKey = MESSAGE_KEY_PREFIX + conversationId;
        Map<Object, Object> data = redisTemplate.opsForHash().entries(convKey);
        if (!data.isEmpty()) {
            String userId = (String) data.get("userId");
            redisTemplate.opsForSet().remove(USER_CONVERSATION_KEY + userId, conversationId);
        }
        redisTemplate.delete(convKey);
        redisTemplate.delete(msgKey);
    }

    public void saveUpLoadMessage(String conversationId, Message message) {
        String key = MESSAGE_KEY_PREFIX + conversationId;
        List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);
        if (messages != null) {
            // Find and update the message in the list
            for (int i = 0; i < messages.size(); i++) {
                Message existing = (Message) messages.get(i);
                if (existing.getId().equals(message.getId())) {
                    redisTemplate.opsForList().set(key, i, message);
                    redisTemplate.expire(key, Duration.ofMinutes(60));
                    return;
                }
            }
        }
        // If not found, append as new
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, Duration.ofMinutes(60));
    }
}