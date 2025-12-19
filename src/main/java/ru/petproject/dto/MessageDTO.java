package ru.petproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.petproject.model.enums.MessageStatus;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;

    private Long senderId;

    private String senderName;

    private Long receiverId;

    private String receiverName;

    private String messageText;

    private Long replyToId;

    private String replyToMessageText;

    private MessageStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;
}
