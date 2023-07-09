package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);

    Film getFilm(Long id);

    Set<Film> getFilms();

    boolean removeFilm(Film film);

    Optional<Film> updateFilm(Film film);
}
