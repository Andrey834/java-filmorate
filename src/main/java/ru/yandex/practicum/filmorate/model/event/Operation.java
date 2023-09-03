package ru.yandex.practicum.filmorate.model.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Operation {
    REMOVE("REMOVE"),
    ADD("ADD"),
    UPDATE("UPDATE");

    private final String value;
}
