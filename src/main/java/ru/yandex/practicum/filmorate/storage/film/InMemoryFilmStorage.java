package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    public final Map<Integer, Film> films = new HashMap<>();
    private Long id = 0L;

    @Override
    public Film addFilm(Film film) {
        final int idFilm = Math.toIntExact(generateId());
        film.setId((long) idFilm);
        films.put(idFilm, film);
        return film;
    }

    @Override
    public Film getFilm(Long id) {
        Integer idFilm = Math.toIntExact(id);
        return films.get(idFilm);
    }

    @Override
    public boolean removeFilm(Film film) {
        Integer idFilm = Math.toIntExact(film.getId());
        if (films.containsKey(idFilm)) {
            films.remove(idFilm);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        Integer idFilm = Math.toIntExact(film.getId());
        if (films.containsKey(idFilm)) {
            films.put(idFilm, film);
            return Optional.of(films.get(idFilm));
        }
        return Optional.empty();
    }

    @Override
    public Set<Film> getFilms() {
        return new HashSet<>(films.values());
    }

    private Long generateId() {
        return ++id;
    }
}
