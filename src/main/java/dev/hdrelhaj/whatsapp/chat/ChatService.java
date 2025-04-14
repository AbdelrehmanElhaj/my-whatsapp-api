package dev.hdrelhaj.whatsapp.chat;

import dev.hdrelhaj.whatsapp.exception.ResourceNotFoundException;
import dev.hdrelhaj.whatsapp.user.User;
import dev.hdrelhaj.whatsapp.user.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final ConversationRepository conversationRepo;

    public MessageResponse sendMessage(Authentication auth, MessageRequest request) {
        User sender = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User receiver = userRepo.findById(request.receiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        // 1. Create or get existing conversation
        Conversation conversation = findOrCreateConversation(sender, receiver);

        // 2. Save message without conversation reference (to avoid flush cycle issue)
        Message message = Message.builder()
                .content(request.content())
                .sender(sender)
                .receiver(receiver)
                .timestamp(LocalDateTime.now())
                .read(false)
                .build();

        message = messageRepo.save(message);

        // 3. Link both ways now that message is persisted
        conversation.setLastMessage(message);
        conversation = conversationRepo.save(conversation);

        message.setConversation(conversation);
        message = messageRepo.save(message);

        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.isRead(),
                sender.getUsername(),
                receiver.getUsername(),
                message.getTimestamp()
        );
    }

    public List<MessageResponse> getChatHistory(Authentication auth, Long withUserId, int page, int size) {
        User currentUser = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User otherUser = userRepo.findById(withUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        return messageRepo
                .findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
                        currentUser, otherUser, otherUser, currentUser, pageable)
                .stream()
                .map(m -> new MessageResponse(
                        m.getId(),
                        m.getContent(),
                        m.isRead(),
                        m.getSender().getUsername(),
                        m.getReceiver().getUsername(),
                        m.getTimestamp()))
                .toList();
    }

    public void markMessageAsRead(Authentication auth, Long messageId) {
        User currentUser = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (!message.getReceiver().equals(currentUser)) {
            throw new AccessDeniedException("You can only mark your received messages as read");
        }

        message.setRead(true);
        messageRepo.save(message);
    }

    private Conversation findOrCreateConversation(User sender, User receiver) {
        return conversationRepo.findByUser1AndUser2(sender, receiver)
                .or(() -> conversationRepo.findByUser2AndUser1(sender, receiver))
                .orElseGet(() -> {
                    Conversation convo = Conversation.builder()
                            .user1(sender)
                            .user2(receiver)
                            .build();
                    return conversationRepo.save(convo);
                });
    }

    public List<ConversationResponse> getUserConversations(Authentication auth) {
        User currentUser = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return conversationRepo.findByUser1OrUser2(currentUser, currentUser).stream()
                .map(convo -> {
                    User otherUser = convo.getOtherUser(currentUser);
                    String lastMsg = convo.getLastMessage() != null
                            ? convo.getLastMessage().getContent()
                            : null;
                    LocalDateTime lastTime = convo.getLastMessage() != null
                            ? convo.getLastMessage().getTimestamp()
                            : null;
                    int unreadCount = messageRepo
                            .countByReceiverAndConversationAndReadFalse(currentUser, convo);

                    return new ConversationResponse(
                            convo.getId(),
                            otherUser.getUsername(),
                            lastMsg,
                            lastTime,
                            unreadCount);
                })
                .toList();
    }

    public void markAllMessagesAsRead(Authentication auth, Long conversationId) {
        User currentUser = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
        Conversation conversation = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
    
        if (!conversation.involves(currentUser)) {
            throw new AccessDeniedException("You are not part of this conversation");
        }
    
        List<Message> unreadMessages = messageRepo.findByReceiverAndConversationAndIsReadFalse(currentUser, conversation);
    
        unreadMessages.forEach(msg -> msg.setRead(true));
        messageRepo.saveAll(unreadMessages);
    }
    
}
