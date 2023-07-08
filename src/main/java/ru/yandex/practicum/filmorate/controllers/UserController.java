package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private InMemoryUserStorage userStorage;

    @Autowired
    public UserController(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user, HttpServletRequest request) {
        return userStorage.createUser(user,request);
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user, HttpServletRequest request) {
        return userStorage.updateUser(user,request);
    }

    @GetMapping()
    public List<User> getUsers() {
        return userStorage.getUsers();
    }
}
