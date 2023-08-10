package ru.yandex.practicum.filmorate.storage.genres;

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

@Component
@RequiredArgsConstructor
public class GenresDbStorageImpl implements GenresStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sql, (rs, rowNum) -> genreBuilder(rs));
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        String sql = "SELECT * FROM GENRE WHERE ID = ?";
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

    private Genre genreBuilder(ResultSet resultSet) throws SQLException {
        return new Genre(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }
}
