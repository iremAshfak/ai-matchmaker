package ru.petproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.petproject.dto.SwipeDTO;
import ru.petproject.dto.SwipeRequest;
import ru.petproject.model.Match;
import ru.petproject.model.User;
import ru.petproject.service.MatchService;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchingService;

    @PostMapping("/swipe")
    public ResponseEntity<SwipeDTO> swipe(
            @RequestBody SwipeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = Long.parseLong(userDetails.getUsername());
        SwipeResult result = matchingService.swipe(
                userId,
                request.getSwipedUserId(),
                request.isMatched()
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/potential")
    public ResponseEntity<List<User>> getPotentialMatches(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<User> potentialMatches = matchingService.getPotentialMatches(userId);
        return ResponseEntity.ok(potentialMatches);
    }

    @GetMapping("/my-matches")
    public ResponseEntity<List<Match>> getMyMatches(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<Match> matches = matchingService.getUserMatches(userId);
        return ResponseEntity.ok(matches);
    }
}
