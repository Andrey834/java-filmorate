package ru.yandex.practicum.filmorate.service.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorageImpl;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorServiceImpl(DirectorStorageImpl directorStorageImpl) {
        this.directorStorage = directorStorageImpl;
    }

    @Override
    public Director createDirector(Director director) {
        validation(director);
        Optional<Director> directorOptional = directorStorage.createDirector(director);

        if (directorOptional.isPresent()) {
            return directorOptional.get();
        } else {
            throw new ValidationException("Такой режиссер уже есть в БД");
        }
    }

    @Override
    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    @Override
    public Director getDirectorById(int id) {
        Optional<Director> directorOptional = directorStorage.getDirectorById(id);

        if (directorOptional.isPresent()) {
            return directorOptional.get();
        } else {
            throw new NotFoundException(String.format("Режиссер с id=%s не найден", id));
        }
    }

    @Override
    public void deleteDirectorById(int id) {
        directorStorage.deleteDirectorById(id);
    }

    @Override
    public void deleteAllDirectors() {
        directorStorage.deleteAllDirectors();
    }

    @Override
    public Director updateDirector(Director director) {
        validation(director);
        Optional<Director> directorOptional = directorStorage.getDirectorById(director.getId());

        if (directorOptional.isPresent()) {
            return directorStorage.updateDirector(director);
        } else {
            throw new NotFoundException(String.format("Режиссер с id=%s не найден", director.getId()));
        }
    }

    private void validation(Director director) {
        if (director.getName().isBlank()) {
            log.error("ValidationException: incorrect name");
            throw new ValidationException("Incorrect name");
        }
        if (director.getName().length() > 100) {
            log.error("ValidationException: incorrect name");
            throw new ValidationException("Incorrect name");
        }
    }
}
