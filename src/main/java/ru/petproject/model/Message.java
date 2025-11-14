package ru.petproject.model;

import java.time.LocalDateTime;

public class Message {
    private Long id;

    private Long fromUserId;

    private Long toUserId;

    private String messageText;

    private LocalDateTime createdAt;
}

