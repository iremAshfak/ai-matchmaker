package ru.petproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.petproject.model.Swipe;
import ru.petproject.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface SwipeRepository extends JpaRepository<Swipe, Long> {

    Optional<Swipe> findByUser1AndUser2(User user1, User user2);

    @Query("SELECT s FROM Swipe s WHERE " +
            "(s.user1.id = :userId1 AND s.user2.id = :userId2) OR " +
            "(s.user1.id = :userId2 AND s.user2.id = :userId1)")
    List<Swipe> findSwipesBetweenUsers(@Param("userId1") Long userId1,
                                       @Param("userId2") Long userId2);

    @Query("SELECT s FROM Swipe s WHERE s.user1.id = :user1Id AND s.swipeResult = true")
    List<Swipe> findLikesByUser(@Param("user1Id") Long user1Id);
}
