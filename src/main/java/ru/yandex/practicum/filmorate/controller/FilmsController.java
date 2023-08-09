package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class FilmsController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Set<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping(value = "/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.findFilmById(id);
    }

    @PutMapping(value = "/{filmId}/like/{userId}")
    public boolean likeFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        return filmService.like(filmId, userId);
    }

    @DeleteMapping(value = "/{filmId}/like/{userId}")
    public boolean deleteLikeFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping(value = "/popular")
    public List<Film> getTopFilm(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getTopRate(count);
    }
}
