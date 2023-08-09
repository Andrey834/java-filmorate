package ru.yandex.practicum.filmorate.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GenresName {
    Comedy("Комедия"),
    Drama("Драма"),
    Cartoon("Мультфильм"),
    Thriller("Триллер"),
    Documentary("Документальный"),
    Action("Боевик");

    private final String value;
}
