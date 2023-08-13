package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Mpa {

    @NonNull
    private Integer id;

    private String name;

    private String description;
}
