package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long id = 0L;

    @Override
    public Film addFilm(Film film) {
        final Long idFilm = generateId();
        film.setId(idFilm);
        films.put(idFilm, film);
        return films.get(idFilm);
    }

    @Override
    public Film getFilm(Long id) {
        return films.get(id);
    }

    @Override
    public boolean removeFilm(Film film) {
        final Long idFilm = film.getId();
        if (films.containsKey(idFilm)) {
            films.remove(idFilm);
            return true;
        }
        return false;
    }

    @Override
    public Film updateFilm(Film film) {
        final Long idFilm = film.getId();
        if (films.containsKey(idFilm)) {
            films.put(idFilm, film);
            return films.get(idFilm);
        }
        return null;
    }

    @Override
    public Set<Film> getFilms() {
        return new HashSet<>(films.values());
    }

    private Long generateId() {
        return ++id;
    }
}
