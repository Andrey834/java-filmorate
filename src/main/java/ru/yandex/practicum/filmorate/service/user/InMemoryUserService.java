package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmptyObjectException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {
    private int id;
    private final UserStorage userStorage;

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
        if (!userStorage.existsById(user.getId())) { // не очень нравится что объект user будет еще раз создан в методе
            log.error("NotFoundException: User with id={} was not found.", user.getId());
            throw new NotFoundException("User was not found.");
        }
        return userStorage.updateUser(user);
    }

    @Override
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public User getUserById(int userId) {
        if (!userStorage.existsById(userId)) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User was not found.");
        }
        return userStorage.getUserById(userId);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (!userStorage.existsById(userId)) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User not found.");
        }
        if (!userStorage.existsById(friendId)) {
            log.error("NotFoundException: Friend with id={} was not found.", friendId);
            throw new NotFoundException("Friend was not found.");
        }

        userStorage.plusFriend(userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (!userStorage.existsById(userId)) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User was not found.");
        }
        if (!userStorage.existsById(friendId)) {
            log.error("NotFoundException: Friend with id={} was not found.", friendId);
            throw new NotFoundException("Friend was not found.");
        }

        userStorage.minusFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        if (!userStorage.existsById(userId)) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User was not found.");
        }

        return userStorage.getFriends(userId);
    }

    @Override
    public List<User> getMutualFriends(int userId, int friendId) {
        if (!userStorage.existsById(userId)) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User was not found.");
        }
        if (!userStorage.existsById(friendId)) {
            log.error("NotFoundException: Friend with id={} was not found.", friendId);
            throw new NotFoundException("Friend was not found.");
        }

        return userStorage.getMutualFriends(userId, friendId);
    }

    @Override
    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
        setId(0);
        log.info("User database was clear");
    }

    private int getNextId() {
        return ++id;
    }

    public void setId(int id) {
        this.id = id;
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
