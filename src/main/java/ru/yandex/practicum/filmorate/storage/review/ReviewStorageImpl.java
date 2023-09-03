package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewFilm;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Component("reviewStorage")
@RequiredArgsConstructor
@Slf4j
public class ReviewStorageImpl implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public ReviewFilm createReview(ReviewFilm reviewFilm) {
        String content = reviewFilm.getContent();
        Boolean isPositive = reviewFilm.getIsPositive();
        int userId = reviewFilm.getUserId();
        int filmId = reviewFilm.getFilmId();
        int useful = reviewFilm.getUseful();

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO REVIEW_FILMS(CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) VALUES(?,?,?,?,?)";

        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, new String[]{"REVIEW_ID"});
            ps.setString(1, content);
            ps.setBoolean(2, isPositive);
            ps.setInt(3, userId);
            ps.setInt(4, filmId);
            ps.setInt(5, useful);
            return ps;
        }, keyHolder);

        int idNewReview = Objects.requireNonNull(keyHolder.getKey()).intValue();

        log.info("User with ID={} CREATE review for film with ID={}", userId, filmId);

        return getReview(idNewReview);
    }

    @Override
    public ReviewFilm editReview(ReviewFilm reviewFilm) {
        int reviewId = reviewFilm.getReviewId();
        String content = reviewFilm.getContent();
        Boolean isPositive = reviewFilm.getIsPositive();

        String sql = "UPDATE REVIEW_FILMS SET content = ?, is_positive = ? WHERE review_id = ?";

        jdbcTemplate.update(sql, content, isPositive, reviewId);

        return getReview(reviewId);
    }

    @Override
    public ReviewFilm getReview(int reviewId) {
        String sql = "SELECT review_id, content, is_positive, user_id, film_id, useful " +
                     "FROM review_films " +
                     "WHERE review_id = ?";

        SqlRowSet reviewRow = jdbcTemplate.queryForRowSet(sql, reviewId);

        if (reviewRow.next()) {
            return new ReviewFilm(
                    reviewRow.getInt("review_id"),
                    reviewRow.getString("content"),
                    reviewRow.getBoolean("is_positive"),
                    reviewRow.getInt("user_id"),
                    reviewRow.getInt("film_id"),
                    reviewRow.getInt("useful")
            );
        }
        return null;
    }

    @Override
    public List<ReviewFilm> getAllReviews(int filmId, int numberOfReviews) {
        String sql = "SELECT * " +
                     "FROM REVIEW_FILMS " +
                     "ORDER BY USEFUL DESC " +
                     "LIMIT ?";

        if (filmId != 0) sql = String.format(
                "SELECT * FROM REVIEW_FILMS %S ORDER BY USEFUL DESC LIMIT ?",
                "WHERE FILM_ID = " + filmId
        );

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new ReviewFilm(
                        rs.getInt("review_id"),
                        rs.getString("content"),
                        rs.getBoolean("is_positive"),
                        rs.getInt("user_id"),
                        rs.getInt("film_id"),
                        rs.getInt("useful")
                ), numberOfReviews
        );
    }

    @Override
    public Boolean deleteReview(int reviewId) {
        String sql = "DELETE FROM review_films WHERE review_id = ?";

        jdbcTemplate.update(sql, reviewId);
        log.info("DELETE review with ID={}", reviewId);

        return true;
    }

    @Override
    public Boolean addLikeOrDislike(int reviewId, int userId, Boolean isPositive) {
        int changeUseful;
        if (isPositive) changeUseful = 1;
        else changeUseful = -1;

        String sql = "INSERT INTO REVIEW_LIKE(USER_ID, REVIEW_ID, IS_POSITIVE) VALUES (?,?,?)";
        String sqlUseful = "UPDATE REVIEW_FILMS SET USEFUL = USEFUL + " + changeUseful + " where REVIEW_ID = ?";

        jdbcTemplate.update(sql, userId, reviewId, isPositive);
        jdbcTemplate.update(sqlUseful, reviewId);
        log.info("make {} LIKE user with id:{} for review with ID={}", isPositive, userId, reviewId);

        return true;
    }

    @Override
    public Boolean removeLikeOrDislike(int reviewId, int userId, Boolean isPositive) {
        int changeUseful;
        if (isPositive) changeUseful = -1;
        else changeUseful = 1;

        String sql = "DELETE FROM REVIEW_LIKE where REVIEW_ID = ? and USER_ID = ?";
        String sqlUseful = "UPDATE REVIEW_FILMS SET USEFUL = USEFUL + " + changeUseful + " where REVIEW_ID = ?";

        jdbcTemplate.update(sql, reviewId, userId);
        jdbcTemplate.update(sqlUseful, reviewId);
        log.info("delete LIKE or DISLIKE user with id:{} for review with ID={}", userId, reviewId);

        return true;
    }
}

