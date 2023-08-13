package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    void addLike(int userId, int filmId);

    void removeLike(int userId, int filmId);

    List<Film> getMostPopularFilms(int count);

    Optional<Film> getFilmById(Integer filmId);

    Set<Genre> getGenresByIds(List<Integer> ids);
}
