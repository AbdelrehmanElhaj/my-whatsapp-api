package dev.hdrelhaj.whatsapp.chat;

import dev.hdrelhaj.whatsapp.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUser1OrUser2(User user1, User user2);
    Optional<Conversation> findByUser1AndUser2(User user1, User user2);
    Optional<Conversation> findByUser2AndUser1(User user2, User user1);
}
