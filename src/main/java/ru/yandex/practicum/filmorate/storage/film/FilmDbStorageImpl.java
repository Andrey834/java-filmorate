package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

        if (!film.getGenres().isEmpty()) {
            List<Integer> genreIds = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList());

            addGenresToFilm(film, genreIds);
        }
        log.info("Создан фильм с ID={}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        String sql = "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA = ?" +
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

        log.info("Обновление фильма с ID={}", film.getId());
        return film;
    }

    public List<Film> getAllFilms() {
        String sql = "SELECT FILM.*, MPA.NAME, MPA.ID, MPA.DESCRIPTION FROM film INNER JOIN MPA ON FILM.MPA = MPA.ID";

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
        String sql = "SELECT f.*, MPA.ID, MPA.NAME, MPA.DESCRIPTION " +
                "FROM FILM AS f " +
                "INNER JOIN MPA ON f.MPA = MPA.ID " +
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

        String sql = "SELECT f.*, m.name AS mpa_name, m.DESCRIPTION AS mpa_description " +
                "FROM film AS f " +
                "JOIN mpa AS m ON f.mpa = m.id " +
                "AND f.id = ?";

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, filmId);

        if (filmRows.next()) {
            Film film = Film.builder()
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate())
                    .mpa(new Mpa(
                            filmRows.getInt("MPA"),
                            filmRows.getString("MPA_NAME"),
                            filmRows.getString("MPA_DESCRIPTION")
                    ))
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

        List<Genre> incomingGenres = new ArrayList<>(film.getGenres());
        List<Genre> filmGenresInDb = getFilmGenres(film.getId());

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

    private void deleteGenresFromFilm(Film film, List<Integer> genresIdsShouldBeDeleted) {
        String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";
        batchUpdate(film, genresIdsShouldBeDeleted, sql);
    }

    private void addGenresToFilm(Film film, List<Integer> genresIdsShouldBeAdded) {
        String sql = "INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?, ?)";
        batchUpdate(film, genresIdsShouldBeAdded, sql);
    }

    private List<Genre> getFilmGenres(Integer filmId) {
        String sql = "SELECT * FROM GENRE JOIN FILM_GENRE ON FILM_GENRE.GENRE_ID = GENRE.ID " +
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
        film.getLikes().addAll(getUserLikes(id));

        return film;
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
