package ru.petproject.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user1;
    @ManyToOne
    private User user2;
    
    private LocalDateTime matchingTime;
    private Boolean active = true;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id) && Objects.equals(user1, match.user1)
                && Objects.equals(user2, match.user2) && Objects.equals(matchingTime, match.matchingTime)
                && Objects.equals(active, match.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user1, user2, matchingTime, active);
    }
}
