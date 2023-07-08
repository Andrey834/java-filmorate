package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        update(film);
        log.info("Обновление информации о фильме: id='{}', название фильма = '{}'",
                film.getId(), film.getName());

        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private void update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            log.error("ValidationException: incorrect id");
            throw new ValidationException("Incorrect id");
        }
    }
}
