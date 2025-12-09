package ru.petproject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.petproject.dto.*;
import ru.petproject.model.User;
import ru.petproject.model.enums.AuthRole;
import ru.petproject.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.petproject.dto.UserProfileResponse.fromUser;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerProfile(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setPreferredGender(request.getPreferredGender());
        user.setAge(request.getAge());
        user.setDescription(request.getDescription());
        user.setRole(AuthRole.USER);

        if (request.getLatitude() != null && request.getLongitude() != null) {
            user.setLatitude(request.getLatitude());
            user.setLongitude(request.getLongitude());
            log.info("Пользователь {} зарегистрировался с координатами", request.getEmail());
        } else {
            log.info("Пользователь {} зарегистрировался без координат", request.getEmail());
        }

        return userRepository.save(user);
    }


    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        if (!request.isLocationValid()) {
            throw new IllegalArgumentException("Необходимо передать обе координаты: latitude и longitude");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Optional.ofNullable(request.getName()).ifPresent(user::setName);
        Optional.ofNullable(request.getGender()).ifPresent(user::setGender);
        Optional.ofNullable(request.getPreferredGender()).ifPresent(user::setPreferredGender);
        Optional.ofNullable(request.getAge()).ifPresent(user::setAge);
        Optional.ofNullable(request.getDescription()).ifPresent(user::setDescription);

        if (request.getLatitude() != null && request.getLongitude() != null) {
            user.setLatitude(request.getLatitude());
            user.setLongitude(request.getLongitude());
            log.info("Пользователь {} обновил координаты", user.getEmail());
        }
        User savedUser = userRepository.save(user);
        return fromUser(savedUser);
    }

    public UserProfileResponse getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Пользователь не авторизован");
        }
        Object principal = authentication.getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        User user = getUserByEmail(email);
        return fromUser(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь с данным email не найден: " + email));
    }

    public UserProfileResponse getProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return fromUser(user);
    }

    public List<UserProfileResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserProfileResponse::fromUser)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    public Double getDistanceBetweenUsers(Long currentUserId, Long userId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (currentUser == null || user == null ||
                !currentUser.hasLocation() || !user.hasLocation()) {
            return null;
        }
        return calculateDistance(
                currentUser.getLatitude(), currentUser.getLongitude(),
                user.getLatitude(), user.getLongitude()
        );
    }

    private static Double calculateDistance (double lat1, double lon1, double lat2, double lon2){
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 100.0) / 100.0;
    }
}