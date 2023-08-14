package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Film {

    private int id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private LocalDate releaseDate;

    private int duration;

    @NonNull
    private Mpa mpa;

    @JsonIgnore
    private final Set<Integer> likes = new HashSet<>();

    private final Set<Genre> genres = new HashSet<>();
}
