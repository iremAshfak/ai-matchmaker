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
import ru.petproject.repository.MatchRepository;
import ru.petproject.repository.SwipeRepository;
import ru.petproject.repository.UserRepository;

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

    public SwipeDTO swipe(Long user1Id, Long user2Id, boolean liked) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new NotFoundException("Первый пользователь не найден"));

        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new NotFoundException("Второй пользователь не найден"));

        Optional<Swipe> existingSwipe = swipeRepository.findByUser1AndUser2(user1, user2);
        if (existingSwipe.isPresent()) {
            throw new DuplicateSwipeException("Уже свайпнули этого пользователя");
        }

        Swipe swipe = new Swipe();
        swipe.setUser1(user1);
        swipe.setUser2(user2);
        swipe.setSwipeResult(liked);
        swipe.setSwipeTime(LocalDateTime.now());
        swipeRepository.save(swipe);
        boolean isMatch = false;
        if (liked) {
            isMatch = checkForMatch(user1, user2);
        }
        return new SwipeDTO(swipe, isMatch);
    }

    private boolean checkForMatch(User user1, User user2) {
        Optional<Swipe> mutualSwipe = swipeRepository.findByUser1AndUser2(user1, user2);
        if (mutualSwipe.isPresent() && mutualSwipe.get().getSwipeResult()) {
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
    }

    public List<User> getPotentialMatches(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userRepository.findPotentialMatches(
                user.getId(),
                user.getPreferredGender(),
                user.getLatitude(),
                user.getLongitude()
        );
    }

    public List<Match> getUserMatches(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return matchRepository.findActiveMatchesByUser(userId);
    }
}
