package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    void addLike(int userId, int filmId);

    void removeLike(int userId, int filmId);

    List<Film> getMostPopularFilms(int count);

    Film getFilmById(int filmId);
}
