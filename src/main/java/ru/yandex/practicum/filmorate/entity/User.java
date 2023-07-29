package ru.yandex.practicum.filmorate.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validator.LoginValidator;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
@EqualsAndHashCode(exclude = {"id"})
public class User {
    private Long id;

    @Email(message = "Wrong format Email")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Login cannot be empty")
    @LoginValidator
    private String login;

    private String name = login;

    @PastOrPresent
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();

    private Set<Long> requestAddFriends = new HashSet<>();

    public void checkFriends(Long id) {
        if (this.requestAddFriends.contains(id) && this.friends.contains(id)) requestAddFriends.remove(id);
    }
}

