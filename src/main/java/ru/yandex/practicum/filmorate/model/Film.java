package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validator.ReleaseValidator;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
@EqualsAndHashCode(exclude = { "id"})
public class Film {
    private Integer id;

    @NotBlank(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @Size(max = 200, message = "Maximum number of characters 200")
    private String description;

    @ReleaseValidator
    private LocalDate releaseDate;

    @Positive
    private int duration;
}
