package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.ReviewFilm;

import java.util.List;

public interface ReviewService {
    ReviewFilm createReview(ReviewFilm reviewFilm);

    ReviewFilm editReview(ReviewFilm reviewFilm);

    Boolean removeReview(int reviewId);

    ReviewFilm getReview(int idReview);

    List<ReviewFilm> getAllReview(int filmId, int numberOfReviews);

    boolean addLikeOrDislikeReview(int idReview, int userId, boolean isLike);

    boolean deleteLikeOrDislikeReview(int idReview, int userId, boolean isLike);
}

