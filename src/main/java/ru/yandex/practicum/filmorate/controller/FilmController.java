package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping()
    public Film createFilm(@RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return filmService.createFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return filmService.updateFilm(film);
    }

    @GetMapping()
    public List<Film> getFilms(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void plusLike(@PathVariable int id, @PathVariable int userId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void minusLike(@PathVariable int id, @PathVariable int userId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        filmService.removeLike(userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(
            @RequestParam(value = "count", required = false, defaultValue = "10") @Positive int count,
            @RequestParam(value = "genreId", required = false, defaultValue = "0") int genreId,
            @RequestParam(value = "year", required = false, defaultValue = "0") int year,
            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return filmService.getTopFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> jointFilms(
            @RequestParam(value = "userId", defaultValue = "0") int userId,
            @RequestParam(value = "friendId", defaultValue = "0") int friendId,
            HttpServletRequest request
    ) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return filmService.getJointFilms(userId, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        filmService.deleteFilm(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getAllDirectorFilmsSorted(
            @PathVariable int directorId,
            @RequestParam(value = "sortBy", defaultValue = "year", required = false) String sortBy,
            HttpServletRequest request
    ) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return filmService.getAllDirectorFilmsSorted(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> getSearchFilms(
            @RequestParam String query,
            @RequestParam String by,
            HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return filmService.getSearchFilms(query, by);
    }
}
