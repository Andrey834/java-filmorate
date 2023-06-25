package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UsersService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class UsersController {
    private UsersService users;

    @PostMapping("/users")
    public User addFilm(@Valid @RequestBody User user) {
        return users.addUser(user);
    }

    @PutMapping("/users")
    public User updateFilm(@Valid @RequestBody User user) {
        return users.updateUser(user);
    }

    @GetMapping("/users")
    public List<User> getFilms() {
        return users.getUsers();
    }
}

