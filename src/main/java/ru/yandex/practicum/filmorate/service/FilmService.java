package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService implements FilmStorage {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public boolean doLike(Long filmId, Long userId) {
        Optional<Film> film = Optional.ofNullable(filmStorage.getFilm(filmId));
        if (film.isEmpty()) throw new FilmNotFoundException("Film not found = " + filmId);

        return filmStorage
                .getFilm(filmId)
                .getLikes()
                .add(userId);
    }

    public boolean removeLike(Long filmId, Long userId) {
        Optional<Film> film = Optional.ofNullable(filmStorage.getFilm(filmId));
        if (film.isEmpty()) throw new FilmNotFoundException("Film not found = " + filmId);

        if (!film.get().getLikes().contains(userId)) {
            throw new UserNotFoundException("User not found = " + userId);
        }

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
        System.out.println(film);
        return filmStorage.addFilm(film);
    }

    @Override
    public Film getFilm(Long id) {
        Optional<Film> film = Optional.ofNullable(filmStorage.getFilm(id));
        if (film.isEmpty()) throw new FilmNotFoundException("Film not found = " + id);
        return film.get();
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
    public Film updateFilm(Film film) {
        Film updateFilm = filmStorage.updateFilm(film);
        if (updateFilm == null) throw new FilmNotFoundException("Film not found = " + film.getId());
        return updateFilm;
    }
}
