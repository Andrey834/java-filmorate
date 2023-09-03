package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.ReviewFilm;
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReviewControllerTest {
    private Film film1;
    private Film film2;
    private User user1;
    private User user2;
    private ReviewFilm reviewFilm1;
    private ReviewFilm reviewFilm2;

    @Autowired
    private FilmController filmController;

    @Autowired
    private UserController userController;

    @Autowired
    private ReviewController reviewController;

    @Autowired
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        film1 = new Film(
                0,
                "HappyThreeFriends 1",
                "animated flash series about the adventures of several animals",
                LocalDate.of(1999, 11, 11),
                90,
                new Mpa(1, "R", "Best series")
        );

        film2 = new Film(
                0,
                "HappyThreeFriends 2",
                "Lisa bought a gun",
                LocalDate.of(2010, 12, 12),
                100,
                new Mpa(1, "R", "Best series")
        );

        user1 = new User(
                0,
                "donutlover@gmail.com",
                "SuperJavaProgrammer2000",
                "Homer",
                LocalDate.of(1993, 11, 15)
        );

        user2 = new User(
                0,
                "snake@gmail.com",
                "angular777",
                "Liza",
                LocalDate.of(2000, 1, 1)
        );

        reviewFilm1 = new ReviewFilm(
                0,
                "this film complete shit",
                false,
                1,
                1,
                0
        );

        reviewFilm2 = new ReviewFilm(
                0,
                "this film good",
                false,
                1,
                1,
                0
        );
    }

    @Test
    void createReview() {
        User newUser = userController.createUser(user1, request);
        Film newFilm = filmController.createFilm(film1, request);

        List<ReviewFilm> listReviews = reviewController.getReviews(1, 10, request);
        int expectedReviewsSize = 0;
        assertEquals(expectedReviewsSize, listReviews.size(), "list must be " + expectedReviewsSize);

        ReviewFilm newReview = reviewController.createReview(reviewFilm1, request);
        assertEquals(reviewFilm1, newReview, "different review, must be identical");

        listReviews = reviewController.getReviews(0, 10, request);
        expectedReviewsSize = 1;
        assertEquals(expectedReviewsSize, listReviews.size(), "list must be " + expectedReviewsSize);

        int expectedId = 1;
        int actualId = newReview.getReviewId();
        assertEquals(expectedId, actualId, "reviewId must be " + expectedId);

        int expectedUseful = 0;
        int actualUseful = newReview.getUseful();
        assertEquals(expectedUseful, actualUseful, "Useful must be " + expectedUseful);

        int expectedFilmId = newFilm.getId();
        assertEquals(expectedFilmId, reviewFilm1.getFilmId(), "filmId must be " + expectedFilmId);

        int expectedUserId = newUser.getId();
        assertEquals(expectedUserId, reviewFilm1.getUserId(), "userId must be " + expectedUserId);
    }

    @Test
    void editReview() {
        userController.createUser(user1, request);
        filmController.createFilm(film1, request);
        ReviewFilm newReview = reviewController.createReview(reviewFilm1, request);

        List<ReviewFilm> listReviews = reviewController.getReviews(0, 10, request);
        int expectedReviewsSize = 1;
        assertEquals(expectedReviewsSize, listReviews.size(), "list must be " + expectedReviewsSize);

        assertTrue(listReviews.contains(newReview), "newReview must be in list");

        ReviewFilm editReview = new ReviewFilm(
                1,
                "edit review",
                true,
                1,
                1,
                0
        );

        ReviewFilm newEditReview = reviewController.editReview(editReview, request);
        assertNotEquals(newReview, newEditReview, "newEditReview must be different");
    }

    @Test
    void deleteReview() {
        userController.createUser(user1, request);
        filmController.createFilm(film1, request);
        ReviewFilm newReview = reviewController.createReview(reviewFilm1, request);

        List<ReviewFilm> listReviews = reviewController.getReviews(0, 10, request);
        int expectedReviewsSize = 1;
        assertEquals(expectedReviewsSize, listReviews.size(), "list must be " + expectedReviewsSize);

        Integer reviewId = newReview.getReviewId();
        reviewController.deleteReview(reviewId, request);
        listReviews = reviewController.getReviews(0, 10, request);
        expectedReviewsSize = 0;
        assertEquals(expectedReviewsSize, listReviews.size(), "list must be " + expectedReviewsSize);
    }

    @Test
    void getReview() {
        userController.createUser(user1, request);
        filmController.createFilm(film1, request);
        reviewController.createReview(reviewFilm1, request);
        reviewController.createReview(reviewFilm2, request);

        int expectedReviewId = 1;
        ReviewFilm newReview = reviewController.getReview(expectedReviewId, request);
        assertEquals(expectedReviewId, newReview.getReviewId(), "Id must be " + expectedReviewId);

        expectedReviewId = 2;
        newReview = reviewController.getReview(expectedReviewId, request);
        assertEquals(expectedReviewId, newReview.getReviewId(), "Id must be " + expectedReviewId);

        expectedReviewId = 3;
        NotFoundException notFoundException = null;
        try {
            reviewController.getReview(expectedReviewId,request);
        } catch (NotFoundException exception) {
            notFoundException = exception;
        }
        assertNotNull(notFoundException, "must be exception");
    }

    @Test
    void getReviews() {
        User newUser1 = userController.createUser(user1, request);
        User newUser2 = userController.createUser(user2, request);
        filmController.createFilm(film1, request);
        ReviewFilm newReviewFilm1 = reviewController.createReview(reviewFilm1, request);
        ReviewFilm newReviewFilm2 = reviewController.createReview(reviewFilm2, request);

        List<ReviewFilm> listReviews = reviewController.getReviews(0, 10, request);
        int expectedReviewsSize = 2;
        assertEquals(expectedReviewsSize, listReviews.size(), "list must be " + expectedReviewsSize);
        assertTrue(listReviews.contains(newReviewFilm1), "list must contain " + newReviewFilm1);
        assertTrue(listReviews.contains(newReviewFilm2), "list must contain " + newReviewFilm2);

        Integer userId1 = newUser1.getId();
        listReviews = reviewController.getReviews(userId1, expectedReviewsSize, request);
        assertEquals(expectedReviewsSize, listReviews.size(), "list must be " + expectedReviewsSize);
        assertTrue(listReviews.contains(newReviewFilm1), "list must contain " + newReviewFilm1);
        assertTrue(listReviews.contains(newReviewFilm2), "list must contain " + newReviewFilm2);

        expectedReviewsSize = 1;
        listReviews = reviewController.getReviews(userId1, expectedReviewsSize, request);
        assertEquals(expectedReviewsSize, listReviews.size(), "list must be " + expectedReviewsSize);
        assertTrue(listReviews.contains(newReviewFilm1), "list must contain " + newReviewFilm1);

        Integer userId2 = newUser2.getId();
        expectedReviewsSize = 0;
        listReviews = reviewController.getReviews(userId2, 10, request);
        assertEquals(expectedReviewsSize, listReviews.size(), "list must be " + expectedReviewsSize);
    }

    @Test
    void addLikeOrDislikeReview() {
        User newUser1 = userController.createUser(user1, request);
        User newUser2 = userController.createUser(user2, request);
        filmController.createFilm(film1, request);
        ReviewFilm newReviewFilm1 = reviewController.createReview(reviewFilm1, request);

        int reviewId1 = newReviewFilm1.getReviewId();
        int userId1 = newUser1.getId();
        int userId2 = newUser2.getId();
        String action;

        int expectedUseful = 0;
        int actualUseful = newReviewFilm1.getUseful();
        assertEquals(expectedUseful, actualUseful, "Useful must be " + expectedUseful);

        action = "like";
        reviewController.addLikeOrDislikeReview(reviewId1, action, userId1, request);
        ReviewFilm likesReview = reviewController.getReview(reviewId1, request);
        expectedUseful = 1;
        actualUseful = likesReview.getUseful();
        assertEquals(expectedUseful, actualUseful, "Useful must be " + expectedUseful);

        action = "dislike";
        reviewController.addLikeOrDislikeReview(reviewId1, action, userId2, request);
        likesReview = reviewController.getReview(reviewId1, request);
        expectedUseful = 0;
        actualUseful = likesReview.getUseful();
        assertEquals(expectedUseful, actualUseful, "Useful must be " + expectedUseful);
    }

    @Test
    void deleteLikeOrDislikeReview() {
        User newUser1 = userController.createUser(user1, request);
        User newUser2 = userController.createUser(user2, request);
        filmController.createFilm(film1, request);
        ReviewFilm newReviewFilm1 = reviewController.createReview(reviewFilm1, request);

        int reviewId1 = newReviewFilm1.getReviewId();
        int userId1 = newUser1.getId();
        int userId2 = newUser2.getId();
        String action;

        action = "like";
        reviewController.addLikeOrDislikeReview(reviewId1, action, userId1, request);

        action = "dislike";
        reviewController.addLikeOrDislikeReview(reviewId1, action, userId2, request);

        ReviewFilm likesReview = reviewController.getReview(reviewId1, request);
        int expectedUseful = 0;
        int actualUseful = likesReview.getUseful();
        assertEquals(expectedUseful, actualUseful, "useful must be " + expectedUseful);

        String removeAction;

        removeAction = "like";
        reviewController.deleteLikeOrDislikeReview(reviewId1, removeAction, userId1, request);
        likesReview = reviewController.getReview(reviewId1, request);
        expectedUseful = -1;
        actualUseful = likesReview.getUseful();
        assertEquals(expectedUseful, actualUseful, "useful must be " + expectedUseful);

        removeAction = "dislike";
        reviewController.deleteLikeOrDislikeReview(reviewId1, removeAction, userId1, request);
        likesReview = reviewController.getReview(reviewId1, request);
        expectedUseful = 0;
        actualUseful = likesReview.getUseful();
        assertEquals(expectedUseful, actualUseful, "useful must be " + expectedUseful);
    }
}