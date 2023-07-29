package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserStorage {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage) {
        this.userStorage = userDbStorage;
    }

    public boolean addFriend(Long userId, Long idFriend) {
        Optional<User> user = Optional.ofNullable(userStorage.findUserById(userId));
        Optional<User> friend = Optional.ofNullable(userStorage.findUserById(idFriend));

        if (user.isEmpty() || friend.isEmpty() || userId.equals(idFriend)) {
            throw new UserNotFoundException("User not found = " + userId + " or " + idFriend);
        } else return userStorage.addFriend(userId, idFriend);
    }

    public boolean removeFriend(Long userId, Long idFriend) {
        Optional<User> user = Optional.ofNullable(userStorage.findUserById(userId));
        Optional<User> friend = Optional.ofNullable(userStorage.findUserById(idFriend));

        if (user.isEmpty() || friend.isEmpty()) {
            throw new UserNotFoundException("User not found = " + userId + " or " + idFriend);
        } else return userStorage.removeFriend(userId, idFriend);
    }

    public List<User> getFriends(Long userId) {
        Optional<User> user = Optional.ofNullable(userStorage.findUserById(userId));
        if (user.isEmpty()) throw new UserNotFoundException("User not found = " + userId);

        List<Long> friends = new ArrayList<>(userStorage.findUserById(userId).getFriends());
        return userStorage
                .findUsers()
                .stream()
                .filter(usr -> friends.contains(usr.getId()))
                .collect(Collectors.toList());

    }

    public List<User> getSameFriends(Long userId, Long friendId) {
        Optional<User> user = Optional.ofNullable(userStorage.findUserById(userId));
        Optional<User> friend = Optional.ofNullable(userStorage.findUserById(friendId));

        if (user.isEmpty() || friend.isEmpty()) {
            throw new UserNotFoundException("User not found = " + userId + " or " + friendId);
        }

        List<Long> usersFriends = new ArrayList<>(userStorage.findUserById(userId).getFriends());
        List<Long> sameFriends = new ArrayList<>(userStorage.findUserById(friendId).getFriends());

        return userStorage
                .findUsers()
                .stream()
                .filter(usr -> usersFriends.contains(usr.getId())
                        && sameFriends.contains(usr.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public User save(@Valid User user) {
        if (user.getName().isEmpty()) user.setName(user.getLogin());
        return userStorage.save(user);
    }

    @Override
    public User findUserById(Long id) {
        Optional<User> user = Optional.ofNullable(userStorage.findUserById(id));
        if (user.isEmpty()) throw new UserNotFoundException("User not found = " + id);
        else return user.get();
    }

    @Override
    public List<User> findUsers() {
        return userStorage.findUsers();
    }

    @Override
    public User update(User user) {
        final Long userId = user.getId();
        Optional<User> updateUser = Optional.ofNullable(userStorage.findUserById(userId));
        if (updateUser.isEmpty()) throw new UserNotFoundException("User not found = " + userId);
        return userStorage.update(user);
    }
}
