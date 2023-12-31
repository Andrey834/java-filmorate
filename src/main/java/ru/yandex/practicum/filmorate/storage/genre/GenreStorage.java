package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    List<Genre> getAllGenres();

    Optional<Genre> getGenreById(int id);

    Set<Genre> getGenresListByIds(List<Integer> ids);
}
