package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.entity.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public User save(User user) {
        final Long idUser = generateId();
        user.setId(idUser);
        if (user.getName().isEmpty()) user.setName(user.getLogin());
        users.put(idUser, user);
        return user;
    }

    @Override
    public User findUserById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        return null;
    }

    public List<User> findUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) {
        final Long idUser = user.getId();
        if (users.containsKey(idUser)) {
            users.put(idUser, user);
            return users.get(idUser);
        }
        return null;
    }

    @Override
    public boolean addFriend(Long idUser, Long idFriend) {
        return false;
    }

    @Override
    public boolean removeFriend(Long idUser, Long idFriend) {
        return true;
    }

    private Long generateId() {
        return ++id;
    }
}
