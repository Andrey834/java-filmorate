package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.sql.ResultSet;

@Component("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDBStorageImpl implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM MPA";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mpaBuilder(rs));
    }

    @Override
    public Optional<Mpa> getMpaById(Integer mpaId) {
        String sql = "SELECT * FROM MPA WHERE ID = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, mpaId);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(
                    mpaRows.getInt("id"),
                    Objects.requireNonNull(mpaRows.getString("name")),
                    mpaRows.getString("description"));
            return Optional.of(mpa);
        } else {
            return Optional.empty();
        }
    }

    private Mpa mpaBuilder(ResultSet resultSet) throws SQLException {
        return new Mpa(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"));
    }
}
