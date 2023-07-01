package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private int id;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping()
    public User createUser(@Valid @RequestBody User user, HttpServletRequest request) {
        if (user == null) throw new ValidationException("User is null");
        user.setId(getNextId());
        validation(user);
        users.put(user.getId(), user);
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user, HttpServletRequest request) {
        if (user == null) throw new ValidationException("User is null");
        validation(user);
        update(user);
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return user;
    }

    @GetMapping()
    public Collection<User> getUsers() {
        return users.values();
    }

    private int getNextId() {
        return ++id;
    }

    private void validation(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@") || !isValidEmail(user.getEmail()))
            throw new ValidationException("Incorrect email");
        if (user.getLogin().isBlank()) throw new ValidationException("Incorrect login");
        if (user.getLogin().contains(" ")) throw new ValidationException("Incorrect login");
        if (user.getBirthday().isAfter(LocalDate.now()))
            throw new ValidationException("Date of birth cannot be in future");
        if (user.getName().isBlank()) user.setName(user.getLogin());
        if (user.getId() < 1) throw new ValidationException("Incorrect Id"); // возможно не нужно
    }

    private void update(User user) {
        //boolean found = false;
        if (users.containsKey(user.getId())) users.put(user.getId(), user);
         else throw new ValidationException("Incorrect Id");
/*        for (Map.Entry<Integer, User> values : users.entrySet()) {
            if (values.getValue().getId() == user.getId()) {
                users.put(getNextId(), user);
                found = true;
            }
        }
        if (!found) users.put(getNextId(), user);*/
    }

    //без этой валидации не проходит тестирование, хотя через постман отрабатывает все ок. Думаю что в постмане
    // при запросе проверка идет на уровне фреймворка, а при тестировании аннотации фреймворка пропускаются и поэтому невалидный
    // емейл пропускался.
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}
