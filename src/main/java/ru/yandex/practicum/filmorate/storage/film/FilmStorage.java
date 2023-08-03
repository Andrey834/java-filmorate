package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    void addLike(int userId, int filmId);

    void removeLike(int userId, int filmId);

    List<Film> getMostPopularFilms(int count);

    Film getFilmById(int filmId);

    boolean existsById(int filmId);

    void deleteAllFilms();
}
