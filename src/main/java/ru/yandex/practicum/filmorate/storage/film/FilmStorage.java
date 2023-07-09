package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    void plusLike(int userId, int filmId);

    void minusLike(int userId, int filmId);

    List<Film> getMostPopularFilms(int count);

    Film getFilmById(int filmId);
}
