package dev.hdrelhaj.whatsapp.chat;

import dev.hdrelhaj.whatsapp.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user1;

    @ManyToOne
    private User user2;

    @OneToOne
    private Message lastMessage;

    public boolean involves(User user) {
        return user.equals(user1) || user.equals(user2);
    }

    public User getOtherUser(User user) {
        if (user.equals(user1)) return user2;
        else if (user.equals(user2)) return user1;
        else return null;
    }
}
