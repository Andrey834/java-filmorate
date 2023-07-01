package ru.yandex.practicum.filmorate.exceptions;

// наследоваться лучше от RunTimeException или от AuthenticationException ?
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
