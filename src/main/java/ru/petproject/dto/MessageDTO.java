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

    private Long fromUserId;

    private String fromUserName;

    private Long toUserId;

    private String toUserName;

    private String messageText;

    private Long replyToId;

    private String replyToMessageText;

    private MessageStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;
}
