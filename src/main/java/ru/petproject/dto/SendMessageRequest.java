package ru.petproject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotNull
    private Long receiverId;

    private String content;

    private Long replyToId;

    private String attachmentUrl;
}
