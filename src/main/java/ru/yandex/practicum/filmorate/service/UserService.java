package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
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
        Optional<User> user = Optional.ofNullable(userStorage.getUser(userId));
        Optional<User> friend = Optional.ofNullable(userStorage.getUser(friendId));

        if (user.isEmpty() || friend.isEmpty()) {
            throw new UserNotFoundException("User not found = " + userId + " or " + friendId);
        }

        userStorage.getUser(userId).getFriends().add(friendId);
        userStorage.getUser(friendId).getFriends().add(userId);
        return true;
    }

    public boolean removeFriend(Long userId, Long friendId) {
        Optional<User> user = Optional.ofNullable(userStorage.getUser(userId));
        Optional<User> friend = Optional.ofNullable(userStorage.getUser(friendId));

        if (user.isEmpty() || friend.isEmpty()) {
            throw new UserNotFoundException("User not found = " + userId + " or " + friendId);
        }

        userStorage.getUser(userId).getFriends().remove(friendId);
        userStorage.getUser(friendId).getFriends().remove(userId);
        return true;
    }

    public List<User> getFriends(Long userId) {
        Optional<User> user = Optional.ofNullable(userStorage.getUser(userId));
        if (user.isEmpty()) throw new UserNotFoundException("User not found = " + userId);

        List<Long> friends = new ArrayList<>(userStorage.getUser(userId).getFriends());
        return userStorage
                .getUsers()
                .stream()
                .filter(usr -> friends.contains(usr.getId()))
                .collect(Collectors.toList());

    }

    public List<User> getSameFriends(Long userId, Long friendId) {
        Optional<User> user = Optional.ofNullable(userStorage.getUser(userId));
        Optional<User> friend = Optional.ofNullable(userStorage.getUser(friendId));

        if (user.isEmpty() || friend.isEmpty()) {
            throw new UserNotFoundException("User not found = " + userId + " or " + friendId);
        }

        List<Long> usersFriends = new ArrayList<>(userStorage.getUser(userId).getFriends());
        List<Long> sameFriends = new ArrayList<>(userStorage.getUser(friendId).getFriends());

        return userStorage
                .getUsers()
                .stream()
                .filter(usr -> usersFriends.contains(usr.getId())
                        && sameFriends.contains(usr.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public User addUser(@Valid User user) {
        return userStorage.addUser(user);
    }

    @Override
    public User getUser(Long id) {
        Optional<User> user = Optional.ofNullable(userStorage.getUser(id));
        if (user.isEmpty()) throw new UserNotFoundException("User not found = " + id);
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
    public User updateUser(User user) {
        final Long userId = user.getId();
        Optional<User> updateUser = Optional.ofNullable(userStorage.getUser(userId));
        if (updateUser.isEmpty()) throw new UserNotFoundException("User not found = " + userId);
        return userStorage.updateUser(user);
    }
}
