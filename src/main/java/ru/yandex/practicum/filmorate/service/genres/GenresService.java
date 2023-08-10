package ru.yandex.practicum.filmorate.service.genres;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenresService {
    Genre getGenreById(int id);

    List<Genre> getAllGenres();
}
