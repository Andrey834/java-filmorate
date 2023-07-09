package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    void plusFriend(int userId, int friendId);

    void minusFriend(int userId, int friendId);

    User getUserById(int userId);

    List<User> getFriends(int userId);

    List<User> getMutualFriends(int userId, int friendId);
}
