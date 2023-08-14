package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("likeDbStorage")
@RequiredArgsConstructor
@Slf4j
public class LikeDbStorageImpl implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    public void addLike(int userId, int filmId) {
        String sql = "INSERT INTO FILM_LIKES (USER_ID, FILM_ID) VALUES(?,?)";
        jdbcTemplate.update(sql, userId, filmId);
        log.info("Пользователь с ID={} поставил лайк фильму с ID={}", userId, filmId);
    }

    public void removeLike(int userId, int filmId) {
        String sql = "DELETE FROM FILM_LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(sql, userId, filmId);
        log.info("Пользователь с ID={} удалил лайк с фильма с ID={}", userId, filmId);
    }
}
