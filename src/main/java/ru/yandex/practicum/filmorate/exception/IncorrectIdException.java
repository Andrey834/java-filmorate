package ru.yandex.practicum.filmorate.exception;

public class IncorrectIdException extends IllegalArgumentException {
    public IncorrectIdException(final String message) {
        super(message);
    }
}
