package dev.hdrelhaj.whatsapp.chat;

import dev.hdrelhaj.whatsapp.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
            User sender1, User receiver1,
            User sender2, User receiver2,
            Pageable pageable);

    int countByReceiverAndConversationAndReadFalse(User receiver, Conversation conversation);

    List<Message> findByReceiverAndConversationAndIsReadFalse(User receiver, Conversation conversation);


}
