package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {
    private int id;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Film createFilm(Film film, HttpServletRequest request) {
        if (film == null) throw new ValidationException("Film is null");
        validation(film);
        film.setId(getNextId());
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film, HttpServletRequest request) {
        if (film == null) throw new ValidationException("Film is null");
        validation(film);
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    private int getNextId() {
        return ++id;
    }

    private void validation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("ValidationException: incorrect name");
            throw new ValidationException("Incorrect name");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.error("ValidationException: incorrect length");
            throw new ValidationException("Incorrect length");
        }
        if (film.getDuration() < 0) {
            log.error("ValidationException: incorrect duration");
            throw new ValidationException("Incorrect duration");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("ValidationException: incorrect release date");
            throw new ValidationException("Incorrect release date");
        }
    }

}
