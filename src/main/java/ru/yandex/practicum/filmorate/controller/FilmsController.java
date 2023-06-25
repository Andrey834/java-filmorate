package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class FilmsController {
    private FilmsService films;

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        return films.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return films.updateFilm(film);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return films.getFilms();
    }
}
