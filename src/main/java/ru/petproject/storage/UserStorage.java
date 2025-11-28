package ru.petproject.storage;

import ru.petproject.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<List<User>> findAll();

    Optional<User> getUserById(Long id);

    User create(User user);

    User update(User newUser);

    void remove(Long id);

    Boolean isUserIdExists(Long id);
}
