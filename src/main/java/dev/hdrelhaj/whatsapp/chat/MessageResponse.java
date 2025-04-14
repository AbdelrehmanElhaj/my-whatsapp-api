package dev.hdrelhaj.whatsapp.chat;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String content,
        boolean read,
        String senderUsername,
        String receiverUsername,
        LocalDateTime timestamp) {
}
