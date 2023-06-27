package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UsersService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Slf4j
public class UsersController {
    private UsersService users;

    @PostMapping
    public User addFilm(@Valid @RequestBody User user) {
        return users.addUser(user);
    }

    @PutMapping
    public User updateFilm(@Valid @RequestBody User user) {
        return users.updateUser(user);
    }

    @GetMapping
    public List<User> getFilms() {
        return users.getUsers();
    }
}

