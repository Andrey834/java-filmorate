package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
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
        if (film == null) {
            log.error("NotFoundException: Film not found.");
            throw new NotFoundException("404. Film not found.");
        }
        validation(film);
        film.setId(getNextId());
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film, HttpServletRequest request) {
        if (film == null) throw new NotFoundException("404. Film not found.");
        validation(film);
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());

        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public void plusLike(int userId, int filmId) {
        var user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("NotFoundException: User not found.");
            throw new NotFoundException("404. User not found.");
        }
        var film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.error("NotFoundException: Film not found.");
            throw new NotFoundException("404. Film not found.");
        }
        filmStorage.plusLike(userId, filmId);
    }

    @Override
    public void minusLike(int userId, int filmId) {
        var user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("NotFoundException: User not found.");
            throw new NotFoundException("404. User not found.");
        }
        var film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.error("NotFoundException: Film not found.");
            throw new NotFoundException("404. Film not found.");
        }
        filmStorage.minusLike(userId, filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) { //или константа = 10 ?
        if (count > filmStorage.getFilms().size()) {
            count = filmStorage.getFilms().size();
        }
        return filmStorage.getMostPopularFilms(count);
    }

    @Override
    public Film getFilmById(int filmId){
        return filmStorage.getFilmById(filmId);
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
