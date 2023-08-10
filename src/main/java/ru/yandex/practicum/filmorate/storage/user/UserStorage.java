package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    Optional<User> updateUser(User user);

    List<User> getAllUsers();

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Optional<User> getUserById(Integer userId);
}
