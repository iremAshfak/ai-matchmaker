package ru.petproject.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.petproject.dto.SwipeDTO;
import ru.petproject.exception.DuplicateSwipeException;
import ru.petproject.exception.NotFoundException;
import ru.petproject.model.Match;
import ru.petproject.model.Swipe;
import ru.petproject.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MatchService {

    @Autowired
    private SwipeRepository swipeRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public SwipeDTO swipe(Long user1Id, Long user2Id, boolean liked) {
        User swiper = userRepository.findById(user1Id)
                .orElseThrow(() -> new NotFoundException("Swiper not found"));

        User swiped = userRepository.findById(user2Id)
                .orElseThrow(() -> new NotFoundException("Swiped user not found"));

        Optional<Swipe> existingSwipe = swipeRepository.findByUsers(user1, user2);
        if (existingSwipe.isPresent()) {
            throw new DuplicateSwipeException("Already swiped this user");
        }

        Swipe swipe = new Swipe();
        swipe.setUser1(swiper);
        swipe.setUser2(swiped);
        swipe.setSwipeResult(liked);
        swipe.setSwipeTime(LocalDateTime.now());
        swipeRepository.save(swipe);

        boolean isMatch = false;
        if (liked) {
            isMatch = checkForMatch(swiper, swiped);
        }

        return new SwipeDTO(swipe, isMatch);
    }

    private boolean checkForMatch(User user1, User user2) {
        Optional<Swipe> mutualSwipe = swipeRepository.findBySwiperAndSwiped(user1, user2);

        if (mutualSwipe.isPresent() && mutualSwipe.get().isMatch()) {
            createMatch(user1, user2);
            return true;
        }

        return false;
    }

    private void createMatch(User user1, User user2) {
        Match match = new Match();
        match.setUser1(user1);
        match.setUser2(user2);
        match.setMatchingTime(LocalDateTime.now());
        matchRepository.save(match);

        notificationService.notifyAboutMatch(user1, user2);
        notificationService.notifyAboutMatch(user2, user1);
    }

    public List<User> getPotentialMatches(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));


        return userRepository.findPotentialMatches(
                user.getId(),
                user.getPreferredGender(),
                user.getLatitude(),
                user.getLongitude()
        );
    }

    public List<Match> getUserMatches(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return matchRepository.findActiveMatchesByUser(user);
    }
}
