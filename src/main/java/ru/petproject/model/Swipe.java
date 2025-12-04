package ru.petproject.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Swipe {
    private Long id;

    private User user1;

    private User user2;

    private Boolean swipeResult;

    private LocalDateTime swipeTime;
}
