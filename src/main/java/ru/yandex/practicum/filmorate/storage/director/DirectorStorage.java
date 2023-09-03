package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {
    Optional<Director> createDirector(Director director);

    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(int id);

    void deleteDirectorById(int id);

    void deleteAllDirectors();

    Director updateDirector(Director director);

    Set<Director> getDirectorsListByIds(List<Integer> ids);
}
