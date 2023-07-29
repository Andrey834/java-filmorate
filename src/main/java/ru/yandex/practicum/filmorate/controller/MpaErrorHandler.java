package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.entity.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;

@RestControllerAdvice(assignableTypes = MpaController.class)
public class MpaErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFoundException(final MpaNotFoundException e) {
        return new ErrorResponse("error", e.getMessage());
    }
}
