package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public User createUser(@Valid @RequestBody User user, HttpServletRequest request) {
        return userService.createUser(user, request);
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user, HttpServletRequest request) {
        return userService.updateUser(user, request);
    }

    @GetMapping()
    public List<User> getUsers(HttpServletRequest request) {
        return userService.getUsers(request);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void plusFriend(@PathVariable int id, @PathVariable int friendId, HttpServletRequest request) {
        userService.plusFriend(id, friendId, request);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void minusFriend(@PathVariable int id, @PathVariable int friendId, HttpServletRequest request) {
        userService.minusFriend(id, friendId, request);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id, HttpServletRequest request) {
        return userService.getFriends(id, request);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId, HttpServletRequest request) {
        return userService.getMutualFriends(id, otherId, request);
    }
}
