package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {

    private int id;

    @NonNull
    @Email
    private String email;

    @NonNull
    private String login;

    private String name;

    @NonNull
    private LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();
}
