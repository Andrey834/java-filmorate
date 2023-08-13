package ru.yandex.practicum.filmorate.storage.genres;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenresStorage {
    List<Genre> getAllGenres();

    Optional<Genre> getGenreById(Integer id);
}
