package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validator.LoginValidator;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
@EqualsAndHashCode(exclude = {"id", "name", "birthday"})
public class User {
    private Integer id;
    @Email(message = "Wrong format Email")
    @NotBlank(message = "Email cannot be empty")
    private String email;
    @NotBlank(message = "Login cannot be empty")
    @LoginValidator
    private String login;
    private String name;
    @Past
    private LocalDate birthday;

    public void checkName() {
        if (this.name == null || this.name.isBlank()) {
            this.name = this.login;
        }
    }
}

