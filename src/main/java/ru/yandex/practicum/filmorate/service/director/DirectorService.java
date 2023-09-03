package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    Director createDirector(Director director);

    List<Director> getAllDirectors();

    Director getDirectorById(int id);

    void deleteDirectorById(int id);

    void deleteAllDirectors();

    Director updateDirector(Director director);
}
