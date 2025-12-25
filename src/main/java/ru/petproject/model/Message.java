package ru.petproject.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.petproject.model.enums.MessageStatus;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User fromUser;

    @ManyToOne
    private User toUser;

    private String messageText;

    @ManyToOne
    private Message replyTo;

    private MessageStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;
}
