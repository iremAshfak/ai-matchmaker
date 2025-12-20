package ru.petproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private User fromUser;

    private User toUser;

    private String messageText;

    private Message replyTo;

    private MessageStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;
}
