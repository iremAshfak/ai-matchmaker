package ru.petproject.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;

    private Long fromUserId;

    private Long toUserId;

    private String messageText;

    private LocalDateTime createdAt;
}

