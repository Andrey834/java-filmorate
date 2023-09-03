package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.like.LikeStorageImpl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("filmStorage")
@RequiredArgsConstructor
@Slf4j
public class FilmStorageImpl implements FilmStorage {
    private final LikeStorageImpl likeDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int getSizeFilms() {
        String sql = "SELECT COUNT(*) FROM FILMS";
        Optional<Integer> size = Optional.ofNullable(jdbcTemplate.queryForObject(sql, int.class));
        return size.orElse(0);
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO FILMS (name, description, duration, release_date, mpa) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setInt(3, film.getDuration());
            ps.setDate(4, Date.valueOf(film.getReleaseDate()));
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        int userId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(userId);

        String sqlGenres = "INSERT INTO FILM_GENRES (film_id, genre_id) VALUES (?, ?)";
        String sqlDirectors = "INSERT INTO FILM_DIRECTORS (film_id, director_id) VALUES (?, ?)";

        List<Integer> genres = film.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        List<Integer> dir = film.getDirectors()
                .stream()
                .map(Director::getId)
                .collect(Collectors.toList());

        batchUpdate(film, genres, sqlGenres);
        batchUpdate(film, dir, sqlDirectors);

        log.info("Создан фильм с ID={}", film.getId());
        return getFilmById(userId);
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILMS " +
                     "SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA = ?" +
                     "WHERE ID = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        updateFilmGenre(film);
        updateFilmDirector(film);

        log.info("Обновление фильма с ID={}", film.getId());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM FILMS AS f, MPA AS m where f.MPA = m.ID";

        return jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs));
    }

    @Override
    public List<Film> getTopFilms(int count, int genreId, int year) {
        String sqlQuery = "";
        if (genreId != 0 && year != 0) {
            sqlQuery = "WHERE EXTRACT(year FROM f.RELEASE_DATE) LIKE COALESCE(?, '%') AND fg.GENRE_ID = ? ";
        } else if (year != 0) {
            sqlQuery = "WHERE EXTRACT(year FROM f.RELEASE_DATE) LIKE COALESCE(?, '%') ";
        } else if (genreId != 0) {
            sqlQuery = "WHERE fg.GENRE_ID = ? ";
        }

        String sql = "SELECT f.*, MPA.ID, MPA.NAME, MPA.DESCRIPTION " +
                     "FROM FILMS AS f " +
                     "INNER JOIN MPA ON f.MPA = MPA.ID " +
                     "LEFT JOIN FILM_GENRES AS fg ON f.ID = fg.FILM_ID " +
                     "LEFT JOIN FILM_LIKES AS fl ON f.ID = fl.FILM_ID " +
                     "LEFT JOIN FILM_DIRECTORS AS fd on f.ID = fd.FILM_ID " +
                     sqlQuery +
                     "GROUP BY f.ID " +
                     "ORDER BY COUNT(fl.USER_ID) DESC " +
                     "LIMIT ? ";

        List<Film> films;

        if (genreId != 0 && year != 0) {
            films = jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs), year, genreId, count);
        } else if (year != 0) {
            films = jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs), year, count);
        } else if (genreId != 0) {
            films = jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs), genreId, count);
        } else {
            films = jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs), count);
        }

        return films;
    }

    @Override
    public Film getFilmById(int filmId) {
        String sql = "SELECT f.*, m.name AS mpa_name, m.DESCRIPTION AS mpa_description " +
                     "FROM FILMS AS f " +
                     "JOIN mpa AS m ON f.mpa = m.id " +
                     "AND f.id = ?";
        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> filmBuilder(rs),
                    filmId);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Film with id=" + filmId + " was not found.");
        }
    }

    @Override
    public void deleteFilm(int id) {
        if (jdbcTemplate.update("DELETE FROM FILMS WHERE ID = ?", id) == 0) {
            String warning = "Missing film with ID: " + id;
            log.warn(warning);
            throw new NotFoundException(warning);
        }
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        query = "%" + query.toLowerCase() + "%";

        switch (by) {
            case "director,title":
                log.info("Поиск совпадений в director и title");
                return getFilmsByNameOrDirector(query);
            case "title,director":
                log.info("Поиск совпадений в title и director");
                return getFilmsByNameOrDirector(query);
            case "director":
                log.info("Поиск совпадений в director");
                return getFilmsByDirector(query);
            case "title":
                log.info("Поиск совпадений в title");
                return getFilmsByName(query);
            default:
                throw new ValidationException("Параметр запроса должен быть director или title или director,title");
        }
    }

    @Override
    public List<Film> getRecommendationsByUserId(int userId) {
        Integer commonLikesUserId = likeDbStorage.getUserIdWithCommonLikes(userId);

        if (commonLikesUserId == 0) {
            return new ArrayList<>();
        }

        String sql = "SELECT * " +
                     "FROM films AS f, mpa AS m " +
                     "WHERE f.mpa = m.id AND f.id IN ( SELECT film_id " +
                     "                                 FROM film_likes " +
                     "                                 WHERE user_id = ? " +
                     "                                 AND film_id NOT IN (SELECT film_id " +
                     "                                                     FROM film_likes " +
                     "                                                     WHERE user_id = ?))";

        return jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs), commonLikesUserId, userId);
    }

    @Override
    public List<Film> getSameLikeFilms(int userId, int friendId) {
        String sql = "SELECT DISTINCT F.*, MP.*, MAX(fl.USER_ID) as likes " +
                     "FROM FILMS as F " +
                     "JOIN MPA MP on F.MPA = MP.ID " +
                     "LEFT JOIN PUBLIC.FILM_LIKES FL on F.ID = FL.FILM_ID " +
                     "WHERE F.ID IN (SELECT FILM_ID " +
                     "               FROM FILM_LIKES" +
                     "               WHERE USER_ID IN (?, ?) " +
                     "               GROUP BY FILM_ID " +
                     "               HAVING COUNT(USER_ID) > 1 " +
                     "               ORDER BY MAX(USER_ID))" +
                     "GROUP BY F.ID " +
                     "ORDER BY MAX(fl.USER_ID) DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs), userId, friendId);
    }

    @Override
    public List<Film> getAllDirectorFilmsSorted(int directorId, String sortBy) {
        String sql = "";
        switch (sortBy) {
            case "year":
                sql = "SELECT * " +
                      "FROM films AS f, mpa AS m " +
                      "WHERE f.mpa = m.id AND f.id IN (SELECT film_id " +
                      "                                FROM film_directors " +
                      "                                WHERE director_id = ?) " +
                      "ORDER BY f.release_date";
                break;
            case "likes":
                sql = "SELECT f.*, m.* " +
                      "FROM films AS f " +
                      "LEFT JOIN mpa AS m ON f.mpa = m.id " +
                      "LEFT JOIN (SELECT film_id, COUNT(user_id) AS likes " +
                      "           FROM film_likes " +
                      "           GROUP BY film_id) AS fl ON f.id = fl.film_id " +
                      "WHERE f.id IN (SELECT film_id " +
                      "               FROM film_directors " +
                      "               WHERE director_id = ?) " +
                      "ORDER BY likes DESC";
                break;
            default:
                throw new ValidationException("Неверный параметр запроса, должен быть year или likes");
        }
        return jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs), directorId);
    }

    private List<Film> getFilmsByName(String query) {
        String sql = "SELECT f.*, MPA.ID, MPA.NAME, MPA.DESCRIPTION " +
                     "FROM FILMS AS f " +
                     "INNER JOIN MPA ON f.MPA = MPA.ID " +
                     "LEFT OUTER JOIN FILM_LIKES AS fl ON f.ID = fl.FILM_ID " +
                     "GROUP BY f.ID " +
                     "HAVING LOWER(f.NAME) LIKE ? " +
                     "ORDER BY COUNT(fl.USER_ID) DESC ";

        return jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs), query);
    }

    private List<Film> getFilmsByDirector(String query) {
        String sql = "SELECT f.*, MPA.ID, MPA.NAME, MPA.DESCRIPTION " +
                     "FROM FILMS AS f " +
                     "INNER JOIN MPA ON f.MPA = MPA.ID " +
                     "LEFT OUTER JOIN FILM_DIRECTORS AS fd ON f.ID = fd.FILM_ID " +
                     "LEFT OUTER JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.ID " +
                     "LEFT OUTER JOIN FILM_LIKES AS fl ON f.ID = fl.FILM_ID " +
                     "GROUP BY f.ID " +
                     "HAVING LOWER(d.NAME) LIKE ? " +
                     "ORDER BY COUNT(fl.USER_ID) DESC ";

        return jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs), query);
    }

    private List<Film> getFilmsByNameOrDirector(String query) {
        String sql = "SELECT f.*, MPA.ID, MPA.NAME, MPA.DESCRIPTION " +
                     "FROM FILMS AS f " +
                     "INNER JOIN MPA ON f.MPA = MPA.ID " +
                     "LEFT OUTER JOIN FILM_DIRECTORS AS fd ON f.ID = fd.FILM_ID " +
                     "LEFT OUTER JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.ID " +
                     "LEFT OUTER JOIN FILM_LIKES AS fl ON f.ID = fl.FILM_ID " +
                     "GROUP BY f.ID " +
                     "HAVING LOWER(f.NAME) LIKE ? OR LOWER(d.NAME) LIKE ?" +
                     "ORDER BY COUNT(fl.USER_ID) DESC ";

        return jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs), query, query);
    }

    private Director directorBuilder(ResultSet resultSet) throws SQLException {
        return new Director(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }

    private List<Integer> getUserLikes(int filmId) {
        String sql = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID = ?";
        return jdbcTemplate.query(sql,
                (resultSet, rowNum) -> resultSet.getInt("user_id"),
                filmId
        );
    }

    private Film filmBuilder(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");

        Mpa mpa = new Mpa(
                rs.getInt("mpa"),
                rs.getString("mpa.name"),
                rs.getString("mpa.description")
        );

        Film film = Film.builder()
                .id(id)
                .mpa(mpa)
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .name(rs.getString("name"))
                .build();

        film.getGenres().addAll(getFilmGenres(id));
        film.getDirectors().addAll(getFilmDirectors(id));
        film.getLikes().addAll(getUserLikes(id));

        return film;
    }

    private void updateFilmGenre(Film film) {

        List<Genre> incomingGenres = new ArrayList<>(film.getGenres());
        List<Genre> filmGenresInDb = getFilmGenres(film.getId());

        if (incomingGenres.isEmpty() && filmGenresInDb.isEmpty()) return;

        List<Integer> genresIdsShouldBeDeleted = filmGenresInDb.stream()
                .filter(genre -> !incomingGenres.contains(genre))
                .map(Genre::getId)
                .collect(Collectors.toList());

        deleteGenresFromFilm(film, genresIdsShouldBeDeleted);

        List<Integer> genresIdsShouldBeAdded = incomingGenres.stream()
                .filter(genre -> !filmGenresInDb.contains(genre))
                .map(Genre::getId)
                .collect(Collectors.toList());

        addGenresToFilm(film, genresIdsShouldBeAdded);
    }

    private void updateFilmDirector(Film film) {

        List<Director> incomingDirectors = new ArrayList<>(film.getDirectors());
        List<Director> filmDirectorsInDb = getFilmDirectors(film.getId());

        if (incomingDirectors.isEmpty() && filmDirectorsInDb.isEmpty()) return;

        List<Integer> directorsIdsShouldBeDeleted = filmDirectorsInDb.stream()
                .filter(director -> !incomingDirectors.contains(director))
                .map(Director::getId)
                .collect(Collectors.toList());

        deleteDirectorsFromFilm(film, directorsIdsShouldBeDeleted);

        List<Integer> directorsIdsShouldBeAdded = incomingDirectors.stream()
                .filter(director -> !filmDirectorsInDb.contains(director))
                .map(Director::getId)
                .collect(Collectors.toList());

        addDirectorsToFilm(film, directorsIdsShouldBeAdded);
    }

    private void deleteGenresFromFilm(Film film, List<Integer> genresIdsShouldBeDeleted) {
        String sql = "DELETE FROM FILM_GENRES WHERE FILM_ID = ? AND GENRE_ID = ?";
        batchUpdate(film, genresIdsShouldBeDeleted, sql);
    }

    private void deleteDirectorsFromFilm(Film film, List<Integer> directorsIdsShouldBeDeleted) {
        String sql = "DELETE FROM FILM_DIRECTORS WHERE FILM_ID = ? AND DIRECTOR_ID = ?";
        batchUpdate(film, directorsIdsShouldBeDeleted, sql);
    }

    private void addGenresToFilm(Film film, List<Integer> genresIdsShouldBeAdded) {
        String sql = "INSERT INTO FILM_GENRES (film_id, genre_id) VALUES (?, ?)";
        batchUpdate(film, genresIdsShouldBeAdded, sql);
    }

    private void addDirectorsToFilm(Film film, List<Integer> directorsIdsShouldBeAdded) {
        String sql = "INSERT INTO FILM_DIRECTORS (film_id, director_id) VALUES (?, ?)";
        batchUpdate(film, directorsIdsShouldBeAdded, sql);
    }

    private List<Genre> getFilmGenres(int filmId) {
        String sql = "SELECT * FROM GENRES " +
                     "JOIN FILM_GENRES ON FILM_GENRES.GENRE_ID = GENRES.ID " +
                     "AND FILM_GENRES.FILM_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> genreBuilder(rs), filmId);
    }

    private List<Director> getFilmDirectors(int filmId) {
        String sql = "SELECT * FROM DIRECTORS " +
                     "JOIN FILM_DIRECTORS ON FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.ID " +
                     "AND FILM_DIRECTORS.FILM_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> directorBuilder(rs), filmId);
    }

    private Genre genreBuilder(ResultSet resultSet) throws SQLException {
        return new Genre(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }

    private void batchUpdate(Film film, List<Integer> filmGenres, String sql) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, film.getId());
                ps.setInt(2, filmGenres.get(i));
            }

            @Override
            public int getBatchSize() {
                return filmGenres.size();
            }
        });
    }
}
