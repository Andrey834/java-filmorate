package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    User addUser(User user);
    Optional<User> getUser(Long id);
    Set<User> getUsers();
    boolean removeUser(User user);
    Optional<User> updateUser(User user);
}
