package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        log.info("Создан пользователь: id='{}', имя = '{}'",
                user.getId(), user.getName());

        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("Обновление информации о пользователе: id='{}', имя = '{}'",
                user.getId(), user.getName());

        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь: id='{}', имя = '{}' добавил в друзья пользователя: id='{}', имя = '{}'",
                userId, user.getName(), friendId, getUserById(friendId).getName());
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = users.get(userId);
        Set<Integer> friends = user.getFriends();

        return friends.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        Set<Integer> otherFriends = user.getFriends();

        return otherFriends.stream()
                .filter(id -> friend.getFriends().contains(id))
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(int userId) {
        return users.containsKey(userId);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }
}
