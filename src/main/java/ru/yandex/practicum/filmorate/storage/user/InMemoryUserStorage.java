package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    public final Map<Integer, User> usersRepo = new HashMap<>();
    private Long id = 0L;

    @Override
    public User addUser(User user) {
        final int idUser = Math.toIntExact(generateId());
        user.setId(idUser);
        if (user.getName().isEmpty()) user.setName(user.getLogin());
        usersRepo.put(idUser, user);
        return user;
    }

    @Override
    public Optional<User> getUser(Long id) {
        return Optional.ofNullable(usersRepo.get(Math.toIntExact(id)));
    }

    public Set<User> getUsers() {
        return new HashSet<>(usersRepo.values());
    }

    @Override
    public Optional<User> updateUser(User user) {
        final Integer idUser = Math.toIntExact(user.getId());
        if (usersRepo.containsKey(idUser)) {
            usersRepo.put(idUser, user);
            return Optional.of(usersRepo.get(idUser));
        }
        return Optional.empty();
    }

    @Override
    public boolean removeUser(User user) {
        final Integer idUser = Math.toIntExact(id);
        if (usersRepo.containsKey(idUser)) {
            usersRepo.remove(idUser);
            return true;
        }
        return false;
    }

    private Long generateId() {
        return ++id;
    }
}
