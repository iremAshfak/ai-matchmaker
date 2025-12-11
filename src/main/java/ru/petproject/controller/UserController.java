package ru.petproject.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.petproject.dto.RegisterRequest;
import ru.petproject.dto.UpdateProfileRequest;
import ru.petproject.dto.UserProfileResponse;
import ru.petproject.model.User;
import ru.petproject.service.UserService;
import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/me")
    public ResponseEntity<User> registerProfile(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerProfile(request);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile( @AuthenticationPrincipal User currentUser,
                                                                @Valid @RequestBody UpdateProfileRequest request) {

        UserProfileResponse updated = userService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal User currentUser) {
        UserProfileResponse profile = userService.getProfileById(currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId,
                                            @AuthenticationPrincipal User currentUser) {
        try {
            UserProfileResponse profile = userService.getProfileById(userId);
            Double distance = userService.getDistanceBetweenUsers(currentUser.getId(), userId);
            if (distance != null) {
                UserProfileResponse response = userService.getAuthenticatedUser();

                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<UserProfileResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteMyProfile(@AuthenticationPrincipal User currentUser) {
        userService.deleteUser(currentUser.getId());
    }
}