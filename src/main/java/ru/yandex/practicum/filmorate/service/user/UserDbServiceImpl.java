package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmptyObjectException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserDbServiceImpl implements UserService {
    private int id;
    private final UserStorage userStorage;

    @Autowired
    public UserDbServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User createUser(User user) {
        if (user == null) {
            log.error("EmptyObjectException: User is null.");
            throw new EmptyObjectException("User was not provided");
        }
        validation(user);
        user.setId(getNextId());

        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        if (user == null) {
            log.error("EmptyObjectException: User is null.");
            throw new EmptyObjectException("User was not provided");
        }
        if (userStorage.getUserById(user.getId()).isEmpty()) {
            log.error("NotFoundException: User with id={} was not found.", user.getId());
            throw new NotFoundException("User was not found.");
        }
        return userStorage.updateUser(user).get();
    }

    @Override
    public List<User> getUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(int userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User was not found.");
        }
        return userStorage.getUserById(userId).get();
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User not found.");
        }
        if (userStorage.getUserById(friendId).isEmpty()) {
            log.error("NotFoundException: Friend with id={} was not found.", friendId);
            throw new NotFoundException("Friend was not found.");
        }
        userStorage.addFriend(userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User was not found.");
        }
        if (userStorage.getUserById(friendId).isEmpty()) {
            log.error("NotFoundException: Friend with id={} was not found.", friendId);
            throw new NotFoundException("Friend was not found.");
        }
        userStorage.removeFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        Optional<User> user = userStorage.getUserById(userId);

        if (user.isEmpty()) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User was not found.");
        }

        return user.get().getFriends().stream()
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(int userId, int friendId) {
        Optional<User> user = userStorage.getUserById(userId);
        Optional<User> friend = userStorage.getUserById(friendId);

        if (user.isEmpty()) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User was not found.");
        }
        if (friend.isEmpty()) {
            log.error("NotFoundException: Friend with id={} was not found.", friendId);
            throw new NotFoundException("Friend was not found.");
        }

        Set<Integer> otherFriends = user.get().getFriends();

        return otherFriends.stream()
                .filter(id -> friend.get().getFriends().contains(id))
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    private int getNextId() {
        return ++id;
    }

    private void validation(User user) {
        if (user.getEmail() == null
                || user.getEmail().isBlank()
                || !isValidEmail(user.getEmail())) {
            log.error("ValidationException: incorrect email");
            throw new ValidationException("Incorrect email");
        }
        if (user.getLogin() == null
                || user.getLogin().isBlank()
                || user.getLogin().contains(" ")) {
            log.error("ValidationException: incorrect login");
            throw new ValidationException("Incorrect login");
        }
        if (user.getBirthday() == null
                || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("ValidationException: Date of birth cannot be in future");
            throw new ValidationException("Date of birth cannot be in future");
        }
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}
