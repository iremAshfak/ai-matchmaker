package ru.petproject.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.petproject.dto.RegisterRequest;
import ru.petproject.dto.UpdateProfileRequest;
import ru.petproject.dto.UserProfileResponse;
import ru.petproject.model.User;
import ru.petproject.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = userService.getCurrentUserId();
        UserProfileResponse updatedUser = userService.updateProfile(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        UserProfileResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

}


