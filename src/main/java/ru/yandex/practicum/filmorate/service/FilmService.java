package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.Mpa;
import ru.yandex.practicum.filmorate.entity.enums.GenresName;
import ru.yandex.practicum.filmorate.entity.enums.MpaName;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService implements FilmStorage {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        this.filmStorage = filmDbStorage;
        this.userStorage = userDbStorage;
    }

    public boolean like(Long filmId, Long userId) {
        Optional<Film> film = Optional.ofNullable(filmStorage.findFilmById(filmId));
        if (film.isEmpty()) throw new FilmNotFoundException("Film not found = " + filmId);
        else return filmStorage.like(filmId, userId);
    }

    public boolean removeLike(Long filmId, Long userId) {
        if (filmStorage.findFilmById(filmId) == null) throw new FilmNotFoundException("Film not found = " + filmId);
        if (userStorage.findUserById(userId) == null) throw new UserNotFoundException("User not found = " + userId);

        return filmStorage.removeLike(filmId, userId);
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
    public Film findFilmById(Long id) {
        Optional<Film> film = Optional.ofNullable(filmStorage.findFilmById(id));
        if (film.isEmpty()) throw new FilmNotFoundException("Film not found = " + id);
        return film.get();
    }

    @Override
    public Set<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmStorage.findFilmById(film.getId()) == null) {
            throw new FilmNotFoundException("Film not found = " + film.getId());
        }
        return filmStorage.updateFilm(film);
    }

    public List<Mpa> getAllMpa() {
        int sizeEnumMpa = MpaName.values().length;
        List<Mpa> result = new ArrayList<>();

        for (int i = 0; i < sizeEnumMpa; i++) {
            result.add(new Mpa(i + 1L));
        }

        return result;
    }

    public List<Genre> getAllGenres() {
        int sizeEnumGenres = GenresName.values().length;
        List<Genre> result = new ArrayList<>();

        for (int i = 0; i < sizeEnumGenres; i++) {
            result.add(new Genre(i + 1L));
        }

        return result;
    }


}
