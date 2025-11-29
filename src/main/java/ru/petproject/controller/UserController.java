package ru.petproject.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDTO create(@Valid @RequestBody UserDTO userDto) {
        return userService.create(userDto);
    }

    @GetMapping("/{userId}")
    public UserDTO getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @PutMapping
    public UserDTO update(@RequestBody UserDTO userDto) {
        return userService.update(userDto);
    }

    @DeleteMapping("/{userId}")
    public void remove(@PathVariable(name = "userId") Long id) {
        userService.remove(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/friends")
    public List<UserDTO> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }
}


