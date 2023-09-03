package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.ReviewFilm;

import java.util.List;

public interface ReviewStorage {
    ReviewFilm createReview(ReviewFilm reviewFilm);

    ReviewFilm editReview(ReviewFilm reviewFilm);

    ReviewFilm getReview(int reviewId);

    List<ReviewFilm> getAllReviews(int filmId, int numberOfReviews);

    Boolean deleteReview(int reviewId);

    Boolean addLikeOrDislike(int reviewId, int userId, Boolean isPositive);

    Boolean removeLikeOrDislike(int reviewId, int userId, Boolean isPositive);
}

