package ru.petproject.dto;

import lombok.Data;

@Data
public class SwipeRequest {
    private Long swipedUserId;
    private boolean matched;
}
