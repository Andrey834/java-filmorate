package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Collections;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film createFilm(Film film) {
        films.put(film.getId(), film);
        log.info("Создан фильм: id='{}', название фильма = '{}'",
                film.getId(), film.getName());

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        log.info("Обновлена информация о фильме: id='{}', название фильма = '{}'",
                film.getId(), film.getName());

        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void plusLike(int userId, int filmId) {
        Film film = getFilmById(filmId);
        film.getLikes().add(userId);
    }

    @Override
    public void minusLike(int userId, int filmId) {
        Film film = getFilmById(filmId);
        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        List<Film> mostPopularFilms = new ArrayList<>(films.values());
        mostPopularFilms.sort(Comparator.comparingInt(film -> film.getLikes().size()));
        Collections.reverse(mostPopularFilms);

        return mostPopularFilms.subList(0, count);
    }

    @Override
    public Film getFilmById(int filmId) {
        return films.get(filmId);
    }

    @Override
    public boolean existsById(int filmId) {
        Film film = getFilmById(filmId);
        return film != null;
    }
}
