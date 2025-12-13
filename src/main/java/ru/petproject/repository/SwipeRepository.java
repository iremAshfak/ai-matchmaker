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

    Optional<Swipe> findByUsers(User user1, User user2);

    List<Swipe> findBySwiperId(Long swiperId);

    List<Swipe> findBySwipedId(Long swipedId);

    @Query("SELECT s FROM Swipe s WHERE " +
            "(s.swiper.id = :userId1 AND s.swiped.id = :userId2) OR " +
            "(s.swiper.id = :userId2 AND s.swiped.id = :userId1)")
    List<Swipe> findSwipesBetweenUsers(@Param("userId1") Long userId1,
                                       @Param("userId2") Long userId2);

    @Query("SELECT s FROM Swipe s WHERE s.swiper.id = :swiperId AND s.liked = true")
    List<Swipe> findLikesByUser(@Param("swiperId") Long swiperId);
}
