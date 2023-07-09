package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (userService.updateUser(user).isEmpty()) {
            throw new UserNotFoundException("User not found = " + user.getId());
        }
        return userService.updateUser(user).get();
    }

    @GetMapping
    public Set<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable Long id) {
        Optional<User> user = userService.getUser(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found = " + id);
        }
        return user.get();
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public boolean addFriend(@Valid @PathVariable Long id, @PathVariable Long friendId) {
        boolean result = userService.addFriend(id, friendId);
        if (!result) throw new UserNotFoundException("User not found = " + id + " or" + friendId);
        return true;
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public boolean deleteFriend(@Valid @PathVariable Long id, @PathVariable Long friendId) {
        return userService.removeFriend(id, friendId);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getFriendsList(@Valid @PathVariable Long id) {
        Optional<List<User>> friends = userService.getFriends(id);
        if (friends.isEmpty()) {
            throw new UserNotFoundException("User not found = " + id);
        }
        return friends.get();
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getSameFriendsList(@PathVariable Long id, @PathVariable Long otherId) {
        if (userService.getSameFriends(id, otherId) == null) {
            throw new UserNotFoundException("User not found = " + id + " or" + otherId);
        }
        return userService.getSameFriends(id, otherId);
    }
}

