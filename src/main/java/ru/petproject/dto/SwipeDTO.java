package ru.petproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.petproject.model.Swipe;

@Data
public class SwipeDTO {
    private Swipe swipe;
    private boolean isMatch;
}
