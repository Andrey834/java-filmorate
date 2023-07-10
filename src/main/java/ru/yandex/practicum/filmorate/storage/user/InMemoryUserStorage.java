package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
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
        update(user);
        log.info("Обновление информации о пользователе: id='{}', имя = '{}'",
                user.getId(), user.getName());

        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void plusFriend(int userId, int friendId) {
        User user = getUserById(userId);
        user.getFriends().add((long) friendId);
        log.info("Пользователь: id='{}', имя = '{}' добавил в друзья пользователя: id='{}', имя = '{}'",
                userId, user.getName(), friendId, getUserById(friendId).getName());
    }

    @Override
    public void minusFriend(int userId, int friendId) {
        User user = getUserById(userId);
        user.getFriends().remove((long) friendId);
    }

    @Override
    public User getUserById(int userId) {
         return users.get(userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = getUserById(userId);
        Set<Long> friends = user.getFriends();

        // пробовал при преобразовании прописать getUserById((int) friendId), не проходит. Как я понимаю это из-за отсутствия
        // вероятного проброса ошибки при переполнении? А в мат. функции проверка уже заложена, поэтому и пропускает, так ?
        // (int)friendId.longValue()) - пробовал такой вариант вместо Math.toIntExact(friendId)). Рабочий, но выглядит страшно :)
        return friends.stream()
                .map(friendId -> getUserById(Math.toIntExact(friendId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Set<Long> friends = user.getFriends();

        return friends.stream()
                .filter(id -> friend.getFriends().contains(id))
                .map(friendsId -> getUserById(Math.toIntExact(friendsId)))
                .collect(Collectors.toList());
    }

    private void update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            log.error("NotFoundException: User not found.");
            throw new NotFoundException("404. User not found.");
        }
    }
}
