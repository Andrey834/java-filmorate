package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage {
    private int id;
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping()
    public Film createFilm(Film film, HttpServletRequest request) {
        if (film == null) throw new ValidationException("Film is null");
        validation(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return film;
    }

    @PutMapping()
    public Film updateFilm(Film film, HttpServletRequest request) {

        if (film == null) throw new ValidationException("Film is null");
        validation(film);
        update(film);
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return film;
    }

    @GetMapping()
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private int getNextId() {
        return ++id;
    }

    private void validation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("ValidationException: incorrect name");
            throw new ValidationException("Incorrect name");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("ValidationException: incorrect length");
            throw new ValidationException("Incorrect length");
        }
        if (film.getDuration() < 0) {
            log.error("ValidationException: incorrect duration");
            throw new ValidationException("Incorrect duration");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("ValidationException: incorrect release date");
            throw new ValidationException("Incorrect release date");
        }
    }

    private void update(Film film) {
        if (films.containsKey(film.getId())) {
            validation(film);
            films.put(film.getId(), film);
        } else {
            log.error("ValidationException: incorrect id");
            throw new ValidationException("Incorrect id");
        }
    }
}
