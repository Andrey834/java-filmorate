package ru.yandex.practicum.filmorate.exception;

// наследоваться лучше от RunTimeException или от AuthenticationException ?
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
