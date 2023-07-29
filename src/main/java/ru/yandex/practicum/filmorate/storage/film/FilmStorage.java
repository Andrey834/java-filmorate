package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.entity.Film;

import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);

    Film findFilmById(Long id);

    Set<Film> getFilms();

    Film updateFilm(Film film);

    boolean like(Long idFilm, Long idUser);

    boolean removeLike(Long idFilm, Long idUser);
}
