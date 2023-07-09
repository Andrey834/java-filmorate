package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface FilmService {
    Film createFilm(Film film, HttpServletRequest request);

    Film updateFilm(Film film, HttpServletRequest request);

    List<Film> getFilms();

    void plusLike(int userId, int filmId);

    void minusLike(int userId, int filmId);

    List<Film> getMostPopularFilms(int count);

    Film getFilmById(int filmId);
}
