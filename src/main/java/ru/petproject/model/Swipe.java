package ru.petproject.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
public class Swipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user1;

    @ManyToOne
    private User user2;

    private Boolean swipeResult;

    private LocalDateTime swipeTime;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Swipe swipe = (Swipe) o;
        return Objects.equals(id, swipe.id) && Objects.equals(user1, swipe.user1)
                && Objects.equals(user2, swipe.user2) && Objects.equals(swipeResult, swipe.swipeResult)
                && Objects.equals(swipeTime, swipe.swipeTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user1, user2, swipeResult, swipeTime);
    }
}
