package ru.yandex.practicum.filmorate.exceptions;

import javax.naming.AuthenticationException;

// наследоваться лучше от RunTimeException или от AuthenticationException ?
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
