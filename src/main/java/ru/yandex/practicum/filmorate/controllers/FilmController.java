package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private int id;
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping()
    public Film createFilm(@RequestBody Film film, HttpServletRequest request) {
        if (film == null) throw new ValidationException("Film is null");
        film.setId(getNextId());
        validation(film);
        films.put(film.getId(), film);
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return film;
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film, HttpServletRequest request) {

        if (film == null) throw new ValidationException("Film is null");
        validation(film);
        update(film);
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return film;
    }

    @GetMapping()
    public Collection<Film> getFilms() {
        return films.values();
    }

    private int getNextId() {
        return ++id;
    }

    private void validation(Film film) {
        if (film.getName().isBlank()) throw new ValidationException("Incorrect name");
        if (film.getDescription().length() > 200) throw new ValidationException("Incorrect length");
        if (film.getDuration() < 0) throw new ValidationException("Incorrect duration");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
            throw new ValidationException("Incorrect release date");
        if (film.getId() < 1) throw new ValidationException("Incorrect Id"); // возможно не нужно
    }

    private void update(Film film) {
/*        boolean found = false;
        if (films.containsKey(film.getId())) films.put(film.getId(),film);
        else films.put(getNextId(), film);
        if (!found) films.put(getNextId(), film);*/
        if (films.containsKey(film.getId())) films.put(film.getId(), film);
        else throw new ValidationException("Incorrect Id");
    }
}
