package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public User addUser(User user) {
        final Long idUser = generateId();
        user.setId(idUser);
        if (user.getName().isEmpty()) user.setName(user.getLogin());
        users.put(idUser, user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        return null;
    }

    public Set<User> getUsers() {
        return new HashSet<>(users.values());
    }

    @Override
    public User updateUser(User user) {
        final Long idUser = user.getId();
        if (users.containsKey(idUser)) {
            users.put(idUser, user);
            return users.get(idUser);
        }
        return null;
    }

    @Override
    public boolean removeUser(User user) {
        final Long idUser = id;
        if (users.containsKey(idUser)) {
            users.remove(idUser);
            return true;
        }
        return false;
    }

    private Long generateId() {
        return ++id;
    }
}
