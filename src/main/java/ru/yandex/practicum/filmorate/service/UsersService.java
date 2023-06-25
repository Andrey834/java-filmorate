package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
//@Validated
public class UsersService {
    private static final Map<Integer, User> users = new HashMap<>();
    private static Integer id = 0;

    public User addUser(User user) {
        final Integer idUser = generateId();
        user.setId(idUser);
        user.checkName();
        users.put(idUser, user);
        return user;
    }

    public User updateUser(User user) {
        if (checkUser(user)) {
            final Integer idUser = user.getId();
            user.checkName();
            users.put(idUser, user);
            return user;
        } else {
            throw new IncorrectIdException("User not found!");
        }
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private Integer generateId() {
        return ++id;
    }

    private boolean checkUser(User user) {
        final Integer idUser = user.getId();
        return users.containsKey(idUser);
    }

    public Map<Integer, User> usersMap() {
        return users;
    }

    public void clearUsers() {
        users.clear();
        id = 0;
    }
}
