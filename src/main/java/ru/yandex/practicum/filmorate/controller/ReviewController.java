package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.review.BadPathException;
import ru.yandex.practicum.filmorate.model.ReviewFilm;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewFilm createReview(@RequestBody ReviewFilm reviewFilm, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return reviewService.createReview(reviewFilm);
    }

    @PutMapping
    public ReviewFilm editReview(@RequestBody ReviewFilm reviewFilm, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return reviewService.editReview(reviewFilm);
    }

    @DeleteMapping("/{id}")
    public boolean deleteReview(@PathVariable(value = "id") int id, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return reviewService.removeReview(id);
    }

    @GetMapping("/{id}")
    public ReviewFilm getReview(@PathVariable(value = "id") int id, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return reviewService.getReview(id);
    }

    @GetMapping()
    public List<ReviewFilm> getReviews(
            @RequestParam(value = "filmId", required = false, defaultValue = "0") int filmId,
            @RequestParam(value = "count", required = false, defaultValue = "10") int count,
            HttpServletRequest request
    ) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );
        return reviewService.getAllReview(filmId, count);
    }

    @PutMapping("/{id}/{action}/{userId}")
    public boolean addLikeOrDislikeReview(
            @PathVariable(value = "id") int id,
            @PathVariable(value = "action") String action,
            @PathVariable(value = "userId") int userId,
            HttpServletRequest request
    ) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );

        boolean isLike;
        if (action.equals("like") || action.equals("dislike")) {
            isLike = action.equals("like");
            return reviewService.addLikeOrDislikeReview(id, userId, isLike);
        } else {
            throw new BadPathException("path <" + request.getRequestURI() + "> not found");
        }
    }

    @DeleteMapping("/{id}/{action}/{userId}")
    public boolean deleteLikeOrDislikeReview(
            @PathVariable(value = "id") int id,
            @PathVariable(value = "action") String action,
            @PathVariable(value = "userId") int userId,
            HttpServletRequest request
    ) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString()
        );

        boolean isLike;
        if (action.equals("like") || action.equals("dislike")) {
            isLike = action.equals("like");
            return reviewService.deleteLikeOrDislikeReview(id, userId, isLike);
        } else {
            throw new BadPathException("path <" + request.getRequestURI() + "> not found");
        }
    }
}

