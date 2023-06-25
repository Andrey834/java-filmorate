package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class FilmsService {
    private static final Map<Integer, Film> films = new HashMap<>();
    private static Integer id = 0;

    public Film addFilm(@Valid Film film) {
        final Integer idFilm = generateId();
        film.setId(idFilm);
        films.put(idFilm, film);
        return film;
    }

    public Film updateFilm(Film film) throws IncorrectIdException {
        if (checkFilm(film)) {
            final Integer idFilm = film.getId();
            films.put(idFilm, film);
            return film;
        } else {
            throw new IncorrectIdException("Movie not found!");
        }
    }

    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private Integer generateId() {
        return ++id;
    }

    private boolean checkFilm(Film film) {
        final Integer idFilm = film.getId();
        return films.containsKey(idFilm);
    }

    public Map<Integer, Film> filmsMap() {
        return films;
    }

    public void clearFilms() {
        films.clear();
        id = 0;
    }
}
