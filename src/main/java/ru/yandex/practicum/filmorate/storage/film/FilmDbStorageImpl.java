package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.util.*;
import java.sql.SQLException;
import java.sql.ResultSet;

@Component("filmDbStorage")
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorageImpl implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO film (id, name, description, duration, release_date, mpa) VALUES(?,?,?,?,?,?)";
        jdbcTemplate.update(sql,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                Date.valueOf(film.getReleaseDate()),
                film.getMpa().getId()
        );

        if (film.getGenres() != null) {
            addGenresToFilm(film);
        }

/*      String sql2 = "SELECT * FROM FILM";
        System.out.println(jdbcTemplate.query(sql2, (rs, rowNum) -> filmBuilder(rs)));*/
        log.info("Создан фильм с ID={}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        String sql = "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA = ?" +
                "WHERE ID = ?";
        jdbcTemplate.update(
                sql
                ,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        updateFilmGenre(film);
        log.info("Обновление фильма с ID={}", film.getId());
        return film;
    }

    public List<Film> getAllFilms() {
        String sql = "SELECT FILM.*, MPA.NAME, MPA.ID FROM film INNER JOIN MPA ON FILM.MPA = MPA.ID";
        return jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs));
    }

    public void addLike(int userId, int filmId) {
        String sql = "INSERT INTO FILM_LIKES (USER_ID, FILM_ID) VALUES(?,?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    public void removeLike(int userId, int filmId) {
        String sql = "DELETE FROM FILM_LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    public List<Film> getMostPopularFilms(int count) {
        String sql = "SELECT f.*, m.ID, m.NAME " +
                "FROM FILM AS f " +
                "INNER JOIN MPA AS m ON f.MPA = m.ID " +
                "LEFT OUTER JOIN FILM_LIKES AS fl ON f.ID = fl.FILM_ID " +
                "GROUP BY f.ID, fl.USER_ID " +
                "ORDER BY COUNT(fl.USER_ID) DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> filmBuilder(rs), count);

        for (Film film : films) {
            film.getGenres().addAll(getFilmGenres(film.getId()));
            film.getLikes().addAll(getUserLikes(film.getId()));
        }

        return films;
    }

    public Optional<Film> getFilmById(Integer filmId) {

        String sql = "SELECT f.*, m.name " +
                "FROM film AS f " +
                "INNER JOIN mpa AS m ON f.mpa = m.id " +
                "AND f.id = ?";

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, filmId);

        /*Mpa mpa = new Mpa(
                filmRows.getInt("id"),
                Objects.requireNonNull(filmRows.getString("name")),
                filmRows.getString("description")
        );*/

        if (filmRows.next()) {
            Film film = Film.builder()
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate())
                    .mpa(new Mpa(
                            filmRows.getInt("mpa"),
                            Objects.requireNonNull(filmRows.getString("NAME")),
                            filmRows.getString("description")))
                    .description(Objects.requireNonNull(filmRows.getString("description")))
                    .name(Objects.requireNonNull(filmRows.getString("name")))
                    .duration(filmRows.getInt("duration"))
                    .id(filmRows.getInt("id"))
                    .build();

            film.getGenres().addAll(getFilmGenres(filmId));
            film.getLikes().addAll(getUserLikes(filmId));
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    public Set<Genre> getGenresByIds(List<Integer> ids) {
        Set<Genre> genres = new HashSet<>();
        for (Integer id : ids) {
            String sql = "SELECT * from GENRE WHERE ID = ?";
            List<Genre> result = jdbcTemplate.query(sql, (rs, rowNum) -> genreBuilder(rs), id);
            if (!result.isEmpty()) {
                genres.addAll(result);
            }
        }
        return genres;
    }

    private void updateFilmGenre(Film film) {
        deleteGenresFromFilm(film);
        addGenresToFilm(film);
    }

    private void deleteGenresFromFilm(Film film) {
        String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    private void addGenresToFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            String sql = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    private List<Genre> getFilmGenres(Integer filmId) {
        String sql = "SELECT * FROM GENRE INNER JOIN FILM_GENRE ON FILM_GENRE.GENRE_ID = GENRE.ID " +
                "AND FILM_GENRE.FILM_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> genreBuilder(rs), filmId);
    }

    private Genre genreBuilder(ResultSet resultSet) throws SQLException {
        return new Genre(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }

    private List<Integer> getUserLikes(Integer filmId) {
        String sql = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID = ?";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> resultSet.getInt("user_id"), filmId);
    }

    private Film filmBuilder(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");

        Mpa mpa = new Mpa(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
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
        film.getLikes().addAll(getUserLikes(id));

        return film;
    }
}
