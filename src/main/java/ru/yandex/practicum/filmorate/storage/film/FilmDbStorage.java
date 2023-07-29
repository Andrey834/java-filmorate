package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = " INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION) VALUES (?, ?, ?, ?)";
        String sqlMpa = "   INSERT INTO FILMS_MPA(FILM_ID, MPA_ID)                       VALUES ( ?, ?)";
        String sqlGenres = "INSERT INTO FILMS_GENRES(FILM_ID, GENRES_ID)                 VALUES ( ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            return ps;
        }, keyHolder);

        Long idNewFilm = Objects.requireNonNull(keyHolder.getKey()).longValue();

        jdbcTemplate.update(sqlMpa, idNewFilm, film.getMpa().getId());

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlGenres, idNewFilm, genre.getId());
        }

        return findFilmById(idNewFilm);
    }

    @Override
    public Film findFilmById(Long id) {
        String sqlQuery = "  select *, MPA_ID from FILMS as F left join  FILMS_MPA    as FM ON F.ID = FM.FILM_ID " +
                          "where" + " F.ID = ?";
        String queryGenre = "select GENRES_ID from FILMS as F join FILMS_GENRES as FG ON F.ID = FG.FILM_ID " +
                            "where" + " F.ID = ?";
        String queryLikes = "select USER_ID from LIKES where FILM_ID = ?";

        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sqlQuery, id);

        Set<Genre> genres = jdbcTemplate.queryForList(queryGenre, Long.class, id)
                .stream().map(Genre::new).collect(Collectors.toSet());

        Set<Long> likes = new HashSet<>(jdbcTemplate.queryForList(queryLikes, Long.class, id));


        if (filmRow.next()) {
            return new Film(
                    filmRow.getLong("id"),
                    filmRow.getString("name"),
                    filmRow.getString("description"),
                    Objects.requireNonNull(filmRow.getDate("release_date")).toLocalDate(),
                    filmRow.getInt("duration"),
                    new Mpa(filmRow.getLong("MPA_ID")),
                    likes,
                    genres
            );
        } else return null;
    }

    @Override
    public Set<Film> getFilms() {
        String sqlQuery = " select *, MPA_ID from FILMS as F LEFT JOIN FILMS_MPA as MPA on F.ID = MPA.FILM_ID";
        String sqlGenres = "select FILM_ID, GENRES_ID from FILMS_GENRES";
        String sqlLikes = "select FILM_ID, USER_ID from LIKES";

        Map<Long, Set<Long>> likes = jdbcTemplate.query(sqlLikes,  (ResultSet rs) -> {
            Map<Long, Set<Long>> results = new HashMap<>();

            while (rs.next()) {
                Long filmId = rs.getLong("FILM_ID");

                if (results.containsKey(filmId)) results.get(filmId).add(filmId);
                else {
                    Set<Long> likesSet = new HashSet<>();
                    likesSet.add(filmId);
                    results.put(filmId, likesSet);
                }
            }
            return results;
        });

        Map<Long, Set<Genre>> genres = jdbcTemplate.query(sqlGenres, (ResultSet rs) -> {
            Map<Long, Set<Genre>> results = new HashMap<>();

            while (rs.next()) {
                Long filmId = rs.getLong("FILM_ID");
                Genre genre = new Genre(rs.getLong("GENRES_ID"));

                if (results.containsKey(filmId)) results.get(filmId).add(genre);
                else {
                    Set<Genre> genresSet = new HashSet<>();
                    genresSet.add(genre);
                    results.put(filmId, genresSet);
                }
            }
            return results;
        });

        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Film(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        Objects.requireNonNull(rs.getDate("RELEASE_DATE")).toLocalDate(),
                        rs.getInt("duration"),
                        new Mpa(rs.getLong("MPA_ID")),
                        new HashSet<>(),
                        new HashSet<>()
                )
        );

        for (Film film : films) {
            if (genres != null && genres.containsKey(film.getId())) {
                film.setGenres(genres.get(film.getId()));
            }

            if (likes != null && likes.containsKey(film.getId())) {
                film.setLikes(likes.get(film.getId()));
            }
        }

        return new HashSet<>(films);
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "   UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ? WHERE ID = ?";
        String sqlMpa = "     UPDATE FILMS_MPA SET MPA_ID = ? WHERE FILM_ID = ?";
        String sqlGenresRm = "DELETE FROM FILMS_GENRES        WHERE FILM_ID = ?";
        String sqlGenres = "  INSERT INTO FILMS_GENRES(FILM_ID, GENRES_ID) VALUES ( ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getId());

            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        jdbcTemplate.update(sqlMpa, film.getMpa().getId(), id);
        jdbcTemplate.update(sqlGenresRm, id);

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlGenres, id, genre.getId());
            }
        }

        return findFilmById(id);
    }

    @Override
    public boolean like(Long idFilm, Long idUser) {

        String sqlQuery = "INSERT INTO LIKES(FILM_ID, USER_ID) VALUES (?, ?)";

        return jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setLong(1, idFilm);
            ps.setLong(2, idUser);
            return ps;
        }) > 0;
    }

    @Override
    public boolean removeLike(Long idFilm, Long idUser) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";

        return jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setLong(1, idFilm);
            ps.setLong(2, idUser);
            return ps;
        }) > 0;
    }
}
