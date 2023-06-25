package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class CorrectlyDateFilm implements ConstraintValidator<ReleaseValidator, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(LocalDate.of(1895,12,27))
                && value.isBefore(LocalDate.now());
    }
}
