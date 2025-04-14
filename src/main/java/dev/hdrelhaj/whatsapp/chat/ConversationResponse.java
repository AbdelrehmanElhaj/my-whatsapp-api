package dev.hdrelhaj.whatsapp.chat;

import java.time.LocalDateTime;

public record ConversationResponse(
    Long id,
    String otherUsername,
    String lastMessage,
    LocalDateTime lastMessageTime,
    int unreadCount
) {}