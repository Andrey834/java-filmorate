package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id", "likes"})
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
    private final Set<Director> directors = new HashSet<>();
}
