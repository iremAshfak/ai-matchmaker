package ru.petproject.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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
}
