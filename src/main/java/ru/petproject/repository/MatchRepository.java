package ru.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.petproject.model.Match;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    @Query("SELECT m FROM Match m WHERE " +
            "(m.user1.id = :userId OR m.user2.id = :userId) AND " +
            "m.active = true")
    List<Match> findActiveMatchesByUser(@Param("userId") Long userId);

    @Query("SELECT m FROM Match m WHERE " +
            "(m.user1.id = :user1Id AND m.user2.id = :user2Id) OR " +
            "(m.user1.id = :user2Id AND m.user2.id = :user1Id)")
    Optional<Match> findMatchBetweenUsers(@Param("user1Id") Long user1Id,
                                          @Param("user2Id") Long user2Id);
}
