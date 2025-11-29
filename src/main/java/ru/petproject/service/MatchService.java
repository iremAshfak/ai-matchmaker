package ru.petproject.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public SwipeResult swipe(Long swiperId, Long swipedId, boolean liked) {
        User swiper = userRepository.findById(swiperId)
                .orElseThrow(() -> new NotFoundException("Swiper not found"));

        User swiped = userRepository.findById(swipedId)
                .orElseThrow(() -> new NotFoundException("Swiped user not found"));

        // Проверяем, не свайпали ли уже
        Optional<Swipe> existingSwipe = swipeRepository.findBySwiperAndSwiped(swiper, swiped);
        if (existingSwipe.isPresent()) {
            throw new DuplicateSwipeException("Already swiped this user");
        }

        // Сохраняем свайп
        Swipe swipe = new Swipe();
        swipe.setSwiper(swiper);
        swipe.setSwiped(swiped);
        swipe.setLiked(liked);
        swipe.setSwipeTime(LocalDateTime.now());
        swipeRepository.save(swipe);

        // Проверяем на мэтч
        boolean isMatch = false;
        if (liked) {
            isMatch = checkForMatch(swiper, swiped);
        }

        return new SwipeResult(swipe, isMatch);
    }

    private boolean checkForMatch(User user1, User user2) {
        // Ищем взаимный лайк
        Optional<Swipe> mutualSwipe = swipeRepository.findBySwiperAndSwiped(user2, user1);

        if (mutualSwipe.isPresent() && mutualSwipe.get().isLiked()) {
            // Создаем мэтч
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

        // Отправляем уведомления
        notificationService.notifyAboutMatch(user1, user2);
        notificationService.notifyAboutMatch(user2, user1);
    }

    public List<User> getPotentialMatches(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Получаем пользователей, которых еще не свайпали
        // с учетом предпочтений по полу, возрасту, локации
        return userRepository.findPotentialMatches(
                user.getId(),
                user.getPreferredGender(),
                user.getLatitude(),
                user.getLongitude()
        );
    }

    public List<Match> getUserMatches(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return matchRepository.findActiveMatchesByUser(user);
    }
}
