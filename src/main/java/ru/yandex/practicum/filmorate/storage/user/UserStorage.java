package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

public interface UserStorage {
    User addUser(User user);

    User getUser(Long id);

    Set<User> getUsers();

    boolean removeUser(User user);

    User updateUser(User user);
}
