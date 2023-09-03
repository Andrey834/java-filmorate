package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("likeStorage")
@RequiredArgsConstructor
@Slf4j
public class LikeStorageImpl implements LikeStorage {
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

    @Override
    public Integer getUserIdWithCommonLikes(int userId) {
        try {
            String sqlCommonLikes = "SELECT fl2.user_id " +
                                    "FROM film_likes AS fl1, film_likes AS fl2 " +
                                    "WHERE fl1.film_id = fl2.film_id " +
                                    "AND fl1.user_id = ? " +
                                    "AND fl1.user_id <> fl2.user_id " +
                                    "GROUP BY fl2.user_id " +
                                    "ORDER BY COUNT(fl2.film_id) DESC " +
                                    "LIMIT 1";
            return jdbcTemplate.queryForObject(sqlCommonLikes, Integer.class, userId);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        }
    }
}
