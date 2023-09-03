package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("userStorage") UserStorage userStorage,
                           @Qualifier("filmStorage") FilmStorage filmStorage,
                           @Qualifier("eventStorage") EventStorage eventStorage
    ) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
        this.filmStorage = filmStorage;
    }

    @Override
    public User createUser(User user) {
        validation(user);
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        validation(user);
        checkUsers(user.getId());
        return userStorage.updateUser(user);
    }

    @Override
    public List<User> getUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(int userId) {
        checkUsers(userId);
        return userStorage.getUserById(userId);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        checkUsers(userId, friendId);
        eventStorage.writeEvent(userId, EventType.FRIEND, Operation.ADD, friendId);
        userStorage.addFriend(userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        checkUsers(userId, friendId);
        eventStorage.writeEvent(userId, EventType.FRIEND, Operation.REMOVE, friendId);
        userStorage.removeFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        checkUsers(userId);
        return userStorage.getFriends(userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        checkUsers(userId, friendId);
        return userStorage.getCommonFriends(userId, friendId);
    }

    @Override
    public void deleteUser(int userId) {
        checkUsers(userId);
        userStorage.deleteUser(userId);
    }

    @Override
    public List<Film> getRecommendationsByUserId(int userId) {
        checkUsers(userId);
        return filmStorage.getRecommendationsByUserId(userId);
    }

    @Override
    public List<Event> getEvents(Integer userId) {
        checkUsers(userId);
        return eventStorage.getEvents(userId);
    }

    @Override
    public void checkUsers(Integer... userNums) {
        List<Integer> users = userStorage.getSelectedUsers(userNums)
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (userNums.length == users.size()) return;

        StringBuilder err = new StringBuilder("NotFoundException: Users was not found with ID=");

        for (int i = 0; i < userNums.length; i++) {
            if (!users.contains(userNums[i])) {
                if (i != 0) err.append(", ");
                err.append(userNums[i]);
            }
        }

        log.error(err.toString());
        throw new NotFoundException("User was not found.");
    }

    private void validation(User user) {
        if (user.getEmail().isBlank() || !isValidEmail(user.getEmail())) {
            log.error("ValidationException: incorrect email");
            throw new ValidationException("Incorrect email");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("ValidationException: incorrect login");
            throw new ValidationException("Incorrect login");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
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
