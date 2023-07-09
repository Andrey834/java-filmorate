package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService implements FilmStorage {
    private final FilmStorage filmStorage;

    public FilmService(@Qualifier("inMemoryFilmStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public boolean doLike(Long filmId, Long userId) {
        return filmStorage
                .getFilm(filmId)
                .getLikes()
                .add(userId);
    }

    public boolean removeLike(Long filmId, Long userId) {
        return filmStorage
                .getFilm(filmId)
                .getLikes()
                .remove(userId);
    }

    public List<Film> getTopRate(Integer count) {
        return filmStorage
                .getFilms()
                .stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(film -> film.getLikes().size())))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    public Film getFilm(Long id) {
        return filmStorage.getFilm(id);
    }

    @Override
    public Set<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public boolean removeFilm(Film film) {
        return filmStorage.removeFilm(film);
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }
}
