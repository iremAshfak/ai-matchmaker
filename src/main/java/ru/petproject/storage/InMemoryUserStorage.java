package ru.petproject.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.petproject.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь с Id {}", user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (users.isEmpty()) {
            log.error("Ошибка при получении списка пользователей");
            return Optional.empty();
        }
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }
        log.error("Ошибка при получении списка пользователей");
        return Optional.empty();
    }

    @Override
    public Optional<List<User>> findAll() {
        if (users.isEmpty()) {
            log.error("Ошибка при получении списка пользователей");
            return Optional.empty();
        } else return Optional.of((List<User>) users.values());
    }

    @Override
    public User update(User newUser) {
        User oldUser = users.get(newUser.getId());
        if (newUser.getName() != null && !newUser.getName().isEmpty()) {
            log.trace("Изменено имя пользователя с Id {}", newUser.getId());
            oldUser.setName(newUser.getName());
        }
        if (newUser.getAge() != null && newUser.getAge() != 0) {
            log.trace("Изменен возраст пользователя с Id {}", newUser.getId());
            oldUser.setAge(newUser.getAge());
        }
        if (newUser.getDescription() != null && !newUser.getDescription().isEmpty()) {
            log.trace("Изменено описание пользователя с Id {}", newUser.getId());
            oldUser.setDescription(newUser.getDescription());
        }
        if (newUser.getCity() != null && !newUser.getCity().isEmpty()) {
            log.trace("Изменен город пользователя с Id {}", newUser.getId());
            oldUser.setCity(newUser.getCity());
        }
        if (newUser.getLatitude() != null && newUser.getLatitude() != 0) {
            log.trace("Изменено местоположение пользователя (широта) с Id {}", newUser.getId());
            oldUser.setLatitude(newUser.getLatitude());
        }
        if (newUser.getLongtitude() != null && newUser.getLongtitude() != 0) {
            log.trace("Изменено местоположение пользователя (долгота) с Id {}", newUser.getId());
            oldUser.setLongtitude(newUser.getLongtitude());
        }
        log.debug("Обновлен пользователь с Id {}", newUser.getId());
        return oldUser;
    }
}
