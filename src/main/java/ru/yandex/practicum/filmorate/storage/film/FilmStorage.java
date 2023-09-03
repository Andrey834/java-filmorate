package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    List<Film> getTopFilms(int count, int genreId, int year);

    Film getFilmById(int filmId);

    void deleteFilm(int id);

    List<Film> searchFilms(String query, String by);

    List<Film> getRecommendationsByUserId(int userId);

    List<Film> getSameLikeFilms(int userId, int friendId);

    List<Film> getAllDirectorFilmsSorted(int directorId, String sortBy);

    int getSizeFilms();
}
