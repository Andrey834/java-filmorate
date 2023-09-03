package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaServiceImpl implements MpaService {
    private final MpaStorage storage;

    @Override
    public Mpa getMpaById(int id) {
        if (storage.getMpaById(id).isEmpty()) {
            log.error("NotFoundException: Genre with id={} was not found.", id);
            throw new NotFoundException("Genre does not exist");
        } else return storage.getMpaById(id).get();
    }

    @Override
    public List<Mpa> getAllMpa() {
        return storage.getAllMpa();
    }
}
