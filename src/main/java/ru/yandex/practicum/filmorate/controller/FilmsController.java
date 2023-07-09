package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmsController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (filmService.updateFilm(film).isEmpty()) {
            throw new FilmNotFoundException("Film not found = " + film.getId());
        }
        return filmService.updateFilm(film).get();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Set<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping(value = "/{id}")
    public Film getFilm(@PathVariable Long id) {
        Optional<Film> film = Optional.ofNullable(filmService.getFilm(id));
        if (film.isEmpty()) {
            throw new FilmNotFoundException("Film not found = " + id);
        }
        return film.get();
    }

    @PutMapping(value = "/{filmId}/like/{userId}")
    public boolean likeFilm(@Valid @PathVariable Long filmId, @PathVariable Long userId) {
        Optional<Film> film = Optional.ofNullable(filmService.getFilm(filmId));
        if (film.isEmpty()) throw new FilmNotFoundException("Film not found = " + filmId);

        boolean result = filmService.doLike(filmId, userId);
        if (!result) throw new UserNotFoundException("User not found = " + userId);

        return filmService.doLike(filmId, userId);
    }

    @DeleteMapping(value = "/{filmId}/like/{userId}")
    public boolean deleteLikeFilm(@Valid @PathVariable Long filmId, @PathVariable Long userId) {
        Optional<Film> film = Optional.ofNullable(filmService.getFilm(filmId));
        if (film.isEmpty()) {
            throw new FilmNotFoundException("Film not found = " + filmId);
        }
        if (!film.get().getLikes().contains(userId)) {
            throw new UserNotFoundException("User not found = " + userId);
        }
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping(value = "/popular")
    public List<Film> deleteLikeFilm
            (@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getTopRate(count);
    }
}
