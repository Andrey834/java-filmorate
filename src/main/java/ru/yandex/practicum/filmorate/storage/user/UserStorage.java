package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    User getUserById(int userId);

    boolean existsById(int userId);

    List<User> getFriends(int userId);

    List<User> getMutualFriends(int userId, int friendId);

    void deleteAllUsers();
}
