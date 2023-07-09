package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {
    private int id;
    private final UserStorage userStorage;

    @Override
    public User createUser(User user, HttpServletRequest request) {
        if (user == null) {
            log.error("NotFoundException: User not found.");
            throw new NotFoundException("404. User not found.");
        }
        validation(user);
        user.setId(getNextId());
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user, HttpServletRequest request) {
        if (user == null) {
            log.error("NotFoundException: User not found.");
            throw new NotFoundException("404. User not found.");
        }
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userStorage.updateUser(user);
    }

    @Override
    public List<User> getUsers(HttpServletRequest request) {
        return userStorage.getUsers();
    }

    @Override
    public User getUserById(int userId){
        return userStorage.getUserById(userId);
    }

    @Override
    public void plusFriend(int userId, int friendId, HttpServletRequest request) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("NotFoundException: User not found.");
            throw new NotFoundException("404. User not found.");
        }
        User friend = userStorage.getUserById(userId);
        if (friend == null) {
            log.error("NotFoundException: Friend not found.");
            throw new NotFoundException("404. Friend not found.");
        }
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        userStorage.plusFriend(userId, friendId);
    }

    @Override
    public void minusFriend(int userId, int friendId, HttpServletRequest request) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("NotFoundException: User not found.");
            throw new NotFoundException("404. User not found.");
        }
        User friend = userStorage.getUserById(userId);
        if (friend == null) {
            log.error("NotFoundException: Friend not found.");
            throw new NotFoundException("404. Friend not found.");
        }
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        userStorage.minusFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId, HttpServletRequest request){
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("NotFoundException: User not found.");
            throw new NotFoundException("404. User not found.");
        }
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userStorage.getFriends(userId);
    }

    @Override
    public List<User> getMutualFriends(int userId, int friendId, HttpServletRequest request){
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("NotFoundException: User not found.");
            throw new NotFoundException("404. User not found.");
        }
        User friend = userStorage.getUserById(userId);
        if (friend == null) {
            log.error("NotFoundException: Friend not found.");
            throw new NotFoundException("404. Friend not found.");
        }
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userStorage.getMutualFriends(userId, friendId);
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
