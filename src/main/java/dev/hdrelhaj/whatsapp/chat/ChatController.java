package dev.hdrelhaj.whatsapp.chat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getMyConversations(Authentication auth) {
        return ResponseEntity.ok(chatService.getUserConversations(auth));
    }

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(
            Authentication auth,
            @RequestBody @Valid MessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(auth, request));
    }

    @GetMapping("/history/{withUserId}")
    public ResponseEntity<List<MessageResponse>> getChatHistory(
            Authentication auth,
            @PathVariable Long withUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(chatService.getChatHistory(auth, withUserId, page, size));
    }

    @PutMapping("/mark-read/{messageId}")
    public ResponseEntity<Void> markAsRead(
            Authentication auth,
            @PathVariable Long messageId) {
        chatService.markMessageAsRead(auth, messageId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/conversations/{conversationId}/mark-all-read")
    public ResponseEntity<Void> markAllMessagesAsRead(
            Authentication auth,
            @PathVariable Long conversationId) {
        chatService.markAllMessagesAsRead(auth, conversationId);
        return ResponseEntity.ok().build();
    }

}
