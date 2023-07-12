package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmptyObjectException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
    public Film createFilm(Film film) {
        if (film == null) {
            log.error("EmptyObjectException: Film is null.");
            throw new EmptyObjectException("Film was not provided.");
        }
        validation(film);
        film.setId(getNextId());

        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (film == null) {
            log.error("EmptyObjectException: Film is null.");
            throw new EmptyObjectException("Film was not provided.");
        }
        if (!filmStorage.existsById(film.getId())) {
            log.error("NotFoundException: Film with id={} was not found.", film.getId());
            throw new NotFoundException("Film was not found.");
        }
        validation(film);

        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public void addLike(int userId, int filmId) {
        if (!userStorage.existsById(userId)) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User was not found.");
        }
        if (!filmStorage.existsById(filmId)) {
            log.error("NotFoundException: Film with id={} was not found.", filmId);
            throw new NotFoundException("Film was not found.");
        }

        filmStorage.addLike(userId, filmId);
    }

    @Override
    public void removeLike(int userId, int filmId) {
        if (!userStorage.existsById(userId)) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User was not found.");
        }
        if (!filmStorage.existsById(filmId)) {
            log.error("NotFoundException: Film with id={} was not found.", filmId);
            throw new NotFoundException("Film was not found.");
        }

        filmStorage.removeLike(userId, filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        if (count > filmStorage.getFilms().size()) {
            log.warn("Указанное пользователем значение count: '{}', значение превышает количество фильмов и будет" +
                            " приравнено к максимальному значению: '{}'",
                    count, filmStorage.getFilms().size());
            count = filmStorage.getFilms().size();
        }

        return filmStorage.getMostPopularFilms(count);
    }

    @Override
    public Film getFilmById(int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            log.error("NotFoundException: Film with id={} was not found.", filmId);
            throw new NotFoundException("Film was not found.");
        }
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public void deleteAllFilms() {
        filmStorage.deleteAllFilms();
        id = 0;
        log.info("Film database was clear");
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
