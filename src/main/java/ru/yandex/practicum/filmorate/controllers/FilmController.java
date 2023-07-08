package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
   private final InMemoryFilmStorage filmStorage;
    @Autowired
    public FilmController(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @PostMapping()
    public Film createFilm(@RequestBody Film film, HttpServletRequest request) {
        return filmStorage.createFilm(film,request);
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film, HttpServletRequest request) {
        return filmStorage.updateFilm(film,request);
    }

    @GetMapping()
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }
}
