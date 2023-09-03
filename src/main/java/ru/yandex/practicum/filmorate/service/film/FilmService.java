package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    void addLike(int userId, int filmId);

    void removeLike(int userId, int filmId);

    List<Film> getTopFilms(int count, int genreId, int year);

    Film getFilmById(int filmId);

    List<Film> getJointFilms(int userId, int friendId);

    void deleteFilm(int id);

    List<Film> getAllDirectorFilmsSorted(int directorId, String sortBy);

    List<Film> getSearchFilms(String query, String by);
}
