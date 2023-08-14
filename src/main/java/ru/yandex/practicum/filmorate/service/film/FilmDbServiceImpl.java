package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmptyObjectException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmDbServiceImpl implements FilmService {
    private int id;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;


    @Autowired
    public FilmDbServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                             @Qualifier("userDbStorage") UserStorage userStorage,
                             @Qualifier("likeDbStorage") LikeStorage likeStorage,
                             @Qualifier("genreDbStorage") GenreStorage genreStorage,
                             @Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Film createFilm(Film film) {
        if (film == null) {
            log.error("EmptyObjectException: Film is null.");
            throw new EmptyObjectException("Film was not provided.");
        }

        validation(film);
        film.setId(getNextId());
        addFilmGenres(film);

        Optional<Mpa> mpa = mpaStorage.getMpaById(film.getMpa().getId());
        if (mpa.isPresent()) film.setMpa(mpa.get());
        else throw new NotFoundException("MPA does not exist");

        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (film == null) {
            log.error("EmptyObjectException: Film is null.");
            throw new EmptyObjectException("Film was not provided.");
        }
        if (filmStorage.getFilmById(film.getId()).isEmpty()) {
            log.error("NotFoundException: Film with id={} was not found.", film.getId());
            throw new NotFoundException("Film was not found.");
        }

        validation(film);
        addFilmGenres(film);

        Optional<Mpa> mpa = mpaStorage.getMpaById(film.getMpa().getId());
        if (mpa.isPresent()) film.setMpa(mpa.get());
        else throw new NotFoundException("MPA does not exist");

        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public void addLike(int userId, int filmId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User was not found.");
        }
        if (filmStorage.getFilmById(filmId).isEmpty()) {
            log.error("NotFoundException: Film with id={} was not found.", filmId);
            throw new NotFoundException("Film was not found.");
        }

        likeStorage.addLike(userId, filmId);
    }

    @Override
    public void removeLike(int userId, int filmId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            log.error("NotFoundException: User with id={} was not found.", userId);
            throw new NotFoundException("User was not found.");
        }
        if (filmStorage.getFilmById(filmId).isEmpty()) {
            log.error("NotFoundException: Film with id={} was not found.", filmId);
            throw new NotFoundException("Film was not found.");
        }

        likeStorage.removeLike(userId, filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        if (count > filmStorage.getAllFilms().size()) {
            log.warn("Указанное пользователем значение count: '{}', значение превышает количество фильмов и будет" +
                            " приравнено к максимальному значению: '{}'",
                    count, filmStorage.getAllFilms().size());
            count = filmStorage.getAllFilms().size();
        }

        return filmStorage.getMostPopularFilms(count);
    }

    @Override
    public Film getFilmById(int filmId) {
        Optional<Film> film = filmStorage.getFilmById(filmId);
        if (film.isEmpty()) {
            log.error("NotFoundException: Film with id={} was not found.", filmId);
            throw new NotFoundException("Film was not found.");
        }
        return filmStorage.getFilmById(filmId).get();
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
}
