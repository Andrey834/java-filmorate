package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("directorStorage")
@RequiredArgsConstructor
@Slf4j
public class DirectorStorageImpl implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Director> createDirector(Director director) {
        log.info("В БД отправлен запрос createDirector с параметром: {}", director);
        if (isDirectorExist(director)) return Optional.empty();
        String sql = "INSERT INTO directors (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, director.getName());
            return preparedStatement;
        }, keyHolder);

        int directorId = Objects.requireNonNull(keyHolder.getKey()).intValue();

        return getDirectorById(directorId);
    }

    @Override
    public List<Director> getAllDirectors() {
        log.info("В БД отправлен запрос getAllDirectors");
        String sql = "SELECT * FROM directors";

        return jdbcTemplate.query(sql, (rs, rowNum) -> directorBuilder(rs));
    }

    @Override
    public Optional<Director> getDirectorById(int id) {
        log.info("В БД отправлен запрос getDirectorById  с параметром {}", id);
        String sql = "SELECT * FROM directors WHERE id = ?";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, id);

        if (rs.next()) {
            return Optional.of(new Director(
                    id,
                    Objects.requireNonNull(rs.getString("name")))
            );
        }

        return Optional.empty();
    }

    @Override
    public void deleteDirectorById(int id) {
        log.info("В БД отправлен запрос deleteDirectorById с id={}", id);
        String sql = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteAllDirectors() {
        log.info("В БД отправлен запрос deleteAllFilms");
        String sql = "DELETE FROM directors";
        jdbcTemplate.execute(sql);
    }

    @Override
    public Director updateDirector(Director director) {
        log.info("В БД отправлен запрос updateDirector с параметром: {}", director);
        String updateRequest = "UPDATE directors " +
                               "SET name = ?" +
                               "WHERE id = ?";
        String selectRequest = "SELECT * FROM directors WHERE id = ?";

        jdbcTemplate.update(updateRequest, director.getName(), director.getId());

        return jdbcTemplate.queryForObject(
                selectRequest,
                (rs, rowNum) -> directorBuilder(rs),
                director.getId()
        );
    }

    @Override
    public Set<Director> getDirectorsListByIds(List<Integer> ids) {
        Set<Director> directors = new HashSet<>();

        String copies = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = String.format("SELECT * FROM DIRECTORS WHERE ID IN (%s)", copies);

        List<Director> result = jdbcTemplate.query(
                sql,
                ids.toArray(),
                (rs, rowNum) -> directorBuilder(rs)
        );

        if (!result.isEmpty()) {
            directors.addAll(result);
        }

        return directors;
    }

    private Director directorBuilder(ResultSet resultSet) throws SQLException {
        return new Director(resultSet.getInt("id"),
                            resultSet.getString("name"));
    }

    private boolean isDirectorExist(Director director) {
        String sql = "SELECT * FROM directors WHERE name = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, director.getName());
        return rs.next();
    }
}
