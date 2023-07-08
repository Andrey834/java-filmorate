package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    User createUser(User user, HttpServletRequest request);

    User updateUser(User user, HttpServletRequest request);

    List<User> getUsers();
}
