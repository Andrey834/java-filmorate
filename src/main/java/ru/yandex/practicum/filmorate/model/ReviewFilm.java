package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = { "reviewId"})
public class ReviewFilm {
    private int reviewId;
    private String content;
    @JsonProperty(value = "isPositive")
    private Boolean isPositive;
    private int userId;
    private int filmId;
    private int useful;
}
