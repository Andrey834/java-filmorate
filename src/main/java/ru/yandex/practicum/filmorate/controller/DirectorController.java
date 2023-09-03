package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    private static final String INFO = "Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'";

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director, HttpServletRequest request) {
        log.info(INFO, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return directorService.createDirector(director);
    }

    @GetMapping
    public List<Director> getAllDirectors(HttpServletRequest request) {
        log.info(INFO, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable(value = "id") int id, HttpServletRequest request) {
        log.info(INFO, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return directorService.getDirectorById(id);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director, HttpServletRequest request) {
        log.info(INFO, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable(value = "id") int id, HttpServletRequest request) {
        log.info(INFO, request.getMethod(), request.getRequestURI(), request.getQueryString());
        directorService.deleteDirectorById(id);
    }

    @DeleteMapping
    void deleteAllDirectors(HttpServletRequest request) {
        log.info(INFO, request.getMethod(), request.getRequestURI(), request.getQueryString());
        directorService.deleteAllDirectors();
    }
}
