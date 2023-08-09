package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenresController {
    private final FilmService filmService;

    @GetMapping(value = "/{id}")
    public Genre getMpa(@PathVariable Long id) {
        return new Genre(id);
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }
}
