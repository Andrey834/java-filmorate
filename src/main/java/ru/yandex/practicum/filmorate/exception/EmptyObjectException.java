package ru.yandex.practicum.filmorate.exception;

public class EmptyObjectException extends RuntimeException {
    public EmptyObjectException(String message) {
        super(message);
    }
}