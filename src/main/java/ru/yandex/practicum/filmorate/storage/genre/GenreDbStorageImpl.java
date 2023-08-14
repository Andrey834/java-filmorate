package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

@Component("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorageImpl implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sql, (rs, rowNum) -> genreBuilder(rs));
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        String sql = "SELECT * FROM GENRES WHERE ID = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sql, id);
        if (genreRow.next()) {
            Genre genre = new Genre(
                    genreRow.getInt("id"),
                    Objects.requireNonNull(genreRow.getString("name"))
            );
            return Optional.of(genre);
        } else {
            return Optional.empty();
        }
    }

    public Set<Genre> getGenresListByIds(List<Integer> ids) {
        Set<Genre> genres = new HashSet<>();

        String copies = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format("SELECT * FROM GENRES WHERE ID IN (%s)", copies);

        List<Genre> result = jdbcTemplate.query(
                sql,
                ids.toArray(),
                (rs, rowNum) -> genreBuilder(rs)
        );

        if (!result.isEmpty()) {
            genres.addAll(result);
        }
        return genres;
    }

    private Genre genreBuilder(ResultSet resultSet) throws SQLException {
        return new Genre(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }
}
