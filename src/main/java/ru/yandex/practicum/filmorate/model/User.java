package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class User {

    private Integer id;

    @NonNull
    @Email
    private String email;

    @NonNull
    private String login;

    private String name;

    @NonNull
    private LocalDate birthday;

    @JsonIgnore
    private final Set<Integer> friends = new HashSet<>();
}
