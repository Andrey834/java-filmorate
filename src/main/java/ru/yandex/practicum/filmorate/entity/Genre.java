package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.entity.enums.GenresName;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    private Long id;
    private String name;

    public Genre(Long id) {
        this.id = id;
        this.name = setGenreName(String.valueOf(id));
    }

    public String setGenreName(String number) {
        switch (number) {
            case "1":
                return GenresName.Comedy.getValue();
            case "2":
                return GenresName.Drama.getValue();
            case "3":
                return GenresName.Cartoon.getValue();
            case "4":
                return GenresName.Thriller.getValue();
            case "5":
                return GenresName.Documentary.getValue();
            case "6":
                return GenresName.Action.getValue();
            default:
                throw new GenreNotFoundException("Genre not found: " + number);
        }
    }
}
