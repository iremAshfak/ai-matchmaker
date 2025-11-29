package ru.petproject.dto;

import lombok.Data;
import ru.petproject.model.Swipe;

@Data
public class SwipeDTO {
    private Swipe swipe;
    private boolean isMatch;

    public SwipeDTO (Swipe swipe, boolean isMatch) {
        this.swipe = swipe;
        this.isMatch = isMatch;
    }
}
