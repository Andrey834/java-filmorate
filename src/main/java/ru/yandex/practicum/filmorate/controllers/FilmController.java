package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping()
    public Film createFilm(@RequestBody Film film, HttpServletRequest request) {
        return filmService.createFilm(film, request);
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film, HttpServletRequest request) {
        return filmService.updateFilm(film, request);
    }

    @GetMapping()
    public List<Film> getFilms(HttpServletRequest request) {
        return filmService.getFilms(request);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id, HttpServletRequest request) {
        return filmService.getFilmById(id, request);
    }

    @PutMapping("/{id}/like/{userId}")
    public void plusLike(@PathVariable int id, @PathVariable int userId, HttpServletRequest request) {
        filmService.plusLike(userId, id, request);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void minusLike(@PathVariable int id, @PathVariable int userId, HttpServletRequest request) {
        filmService.minusLike(userId, id, request);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10") int count, HttpServletRequest request) {
        return filmService.getMostPopularFilms(count, request);
    }
}
