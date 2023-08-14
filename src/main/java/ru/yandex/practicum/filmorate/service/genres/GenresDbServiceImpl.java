package ru.yandex.practicum.filmorate.service.genres;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenresDbServiceImpl implements GenresService {
    private final GenreStorage storage;

    @Override
    public Genre getGenreById(int id) {
        if (storage.getGenreById(id).isEmpty()) {
            log.error("NotFoundException: Genre with id={} was not found.", id);
            throw new NotFoundException("Genre does not exist");
        } else return storage.getGenreById(id).get();
    }

    @Override
    public List<Genre> getAllGenres() {
        return storage.getAllGenres();
    }
}
