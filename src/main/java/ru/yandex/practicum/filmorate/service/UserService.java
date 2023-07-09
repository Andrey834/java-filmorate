package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserStorage {
    private final UserStorage userStorage;

    public UserService(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean addFriend(Long userId, Long friendId) {
        if (userStorage.getUser(userId).isEmpty()) return false;
        if (userStorage.getUser(friendId).isEmpty()) return false;
        userStorage.getUser(userId).get().getFriends().add(friendId);
        userStorage.getUser(friendId).get().getFriends().add(userId);
        return true;
    }

    public boolean removeFriend(Long userId, Long friendId) {
        if (userStorage.getUser(userId).isEmpty()) return false;
        if (userStorage.getUser(friendId).isEmpty()) return false;
        userStorage.getUser(userId).get().getFriends().remove(friendId);
        userStorage.getUser(friendId).get().getFriends().remove(userId);
        return true;
    }

    public Optional<List<User>> getFriends(Long userId) {
        if (userStorage.getUser(userId).isPresent()) {
            List<Long> friends = new ArrayList<>(userStorage.getUser(userId).get().getFriends());
            return Optional.of(userStorage.getUsers().stream().filter(user -> friends.contains((long) user.getId())).collect(Collectors.toList()));
        }
        return Optional.empty();
    }

    public List<User> getSameFriends(Long userId, Long friendId) {
        if (userStorage.getUser(userId).isEmpty()
                || userStorage.getUser(friendId).isEmpty()) return null;

        List<Long> usersFriends = new ArrayList<>(userStorage.getUser(userId).get().getFriends());
        List<Long> sameFriends = new ArrayList<>(userStorage.getUser(friendId).get().getFriends());

        return userStorage
                .getUsers()
                .stream()
                .filter(user -> usersFriends.contains((long) user.getId())
                        && sameFriends.contains((long) user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    @Override
    public Optional<User> getUser(Long id) {
        return userStorage.getUser(id);
    }

    @Override
    public Set<User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public boolean removeUser(User user) {
        return userStorage.removeUser(user);
    }

    @Override
    public Optional<User> updateUser(User user) {
        return userStorage.updateUser(user);
    }
}
