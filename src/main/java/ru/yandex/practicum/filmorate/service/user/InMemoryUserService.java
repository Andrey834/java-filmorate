package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InMemoryUserService implements UserService {
    private int id;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user, HttpServletRequest request) {
        if (user == null) throw new ValidationException("User is null");
        validation(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return user;
    }

    @Override
    public User updateUser(User user, HttpServletRequest request) {
        if (user == null) throw new ValidationException("User is null");
        update(user);
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
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

    private void update(User user) {
        if (users.containsKey(user.getId())) {
            validation(user);
            users.put(user.getId(), user);
        } else {
            log.error("ValidationException: incorrect id");
            throw new ValidationException("Incorrect id");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}
