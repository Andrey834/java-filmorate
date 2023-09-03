package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int friendId);

    User getUserById(int userId);

    void deleteUser(int id);

    List<Film> getRecommendationsByUserId(int userId);

    List<Event> getEvents(Integer userId);

    void checkUsers(Integer... userNums);
}
