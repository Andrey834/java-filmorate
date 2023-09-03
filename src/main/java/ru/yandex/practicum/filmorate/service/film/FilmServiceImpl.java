package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmptyObjectException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.service.director.DirectorService;
import ru.yandex.practicum.filmorate.service.director.DirectorServiceImpl;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaStorage mpaStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;
    private final DirectorService directorService;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmStorage") FilmStorage filmStorage,
                           @Qualifier("userService") UserServiceImpl userService,
                           @Qualifier("likeStorage") LikeStorage likeStorage,
                           @Qualifier("genreStorage") GenreStorage genreStorage,
                           @Qualifier("mpaStorage") MpaStorage mpaStorage,
                           @Qualifier("eventStorage") EventStorage eventStorage,
                           @Qualifier("directorStorage") DirectorStorage directorStorage,
                           DirectorServiceImpl directorServiceImpl
    ) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.eventStorage = eventStorage;
        this.directorStorage = directorStorage;
        this.directorService = directorServiceImpl;
        this.userService = userService;
    }

    @Override
    public Film createFilm(Film film) {
        validation(film);
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        filmStorage.getFilmById(film.getId());
        validation(film);
        addFilmGenres(film);
        addFilmDirectors(film);
        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public void addLike(int userId, int filmId) {
        userService.checkUsers(userId);
        filmStorage.getFilmById(filmId);
        eventStorage.writeEvent(userId, EventType.LIKE, Operation.ADD, filmId);
        likeStorage.addLike(userId, filmId);
    }

    @Override
    public void removeLike(int userId, int filmId) {
        userService.checkUsers(userId);
        filmStorage.getFilmById(filmId);
        eventStorage.writeEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
        likeStorage.removeLike(userId, filmId);
    }

    @Override
    public List<Film> getTopFilms(int count, int genreId, int year) {
        int quantityFilms = filmStorage.getSizeFilms();
        if (count > quantityFilms) log.warn("Maximum value for COUNT: {}", quantityFilms);

        return filmStorage.getTopFilms(count, genreId, year);
    }

    @Override
    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }

    @Override
    public List<Film> getAllDirectorFilmsSorted(int directorId, String sortBy) {
        directorService.getDirectorById(directorId);
        return filmStorage.getAllDirectorFilmsSorted(directorId, sortBy);
    }

    @Override
    public List<Film> getSearchFilms(String query, String by) {
        return filmStorage.searchFilms(query, by);
    }

    @Override
    public List<Film> getJointFilms(int userId, int friendId) {
        userService.checkUsers(userId, friendId);
        return filmStorage.getSameLikeFilms(userId, friendId);
    }

    private void validation(Film film) {
        if (film == null) {
            throw new EmptyObjectException("Film was not provided.");
        }
        if (film.getName().isBlank()) {
            log.error("ValidationException: incorrect name");
            throw new ValidationException("Incorrect name");
        }
        if (film.getDescription().length() > 200) {
            log.error("ValidationException: incorrect length");
            throw new ValidationException("Incorrect length");
        }
        if (film.getDuration() < 0) {
            log.error("ValidationException: incorrect duration");
            throw new ValidationException("Incorrect duration");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("ValidationException: incorrect release date");
            throw new ValidationException("Incorrect release date");
        }
        if (mpaStorage.getMpaById(film.getMpa().getId()).isEmpty()) {
            throw new NotFoundException("MPA does not exist");
        }
    }

    private void addFilmGenres(Film film) {
        if (film.getGenres() != null) {
            Set<Genre> genresSet = film.getGenres();

            List<Integer> genresIds = genresSet.stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .map(Genre::getId)
                    .distinct()
                    .collect(Collectors.toList());

            Set<Genre> genres = genreStorage.getGenresListByIds(genresIds);
            if (genres.size() != genresIds.size()) {
                throw new NotFoundException("Genre doesn't exist");
            }
            film.getGenres().clear();
            film.getGenres().addAll(genres);
        }
    }

    private void addFilmDirectors(Film film) {
        if (film.getDirectors() != null) {
            Set<Director> directorsSet = film.getDirectors();

            List<Integer> directorsIds = directorsSet.stream()
                    .sorted(Comparator.comparing(Director::getId))
                    .map(Director::getId)
                    .distinct()
                    .collect(Collectors.toList());

            Set<Director> directors = directorStorage.getDirectorsListByIds(directorsIds);
            if (directors.size() != directorsIds.size()) {
                throw new NotFoundException("Director doesn't exist");
            }
            film.getDirectors().clear();
            film.getDirectors().addAll(directors);
        }
    }
}
