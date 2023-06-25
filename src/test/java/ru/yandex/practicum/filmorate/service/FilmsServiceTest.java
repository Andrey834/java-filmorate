package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FilmsServiceTest extends FilmorateApplicationTests {
    Film film1;
    Film film2;
    FilmsService service;
    Map<Integer, Film> films;

    @BeforeEach
    public void setUp() {
        service = new FilmsService();
        film1 = new Film(0, "film1", "desc1", LocalDate.of(2015,12,12), 120);
        film2 = new Film(0, "film2", "desc2", LocalDate.of(2017,12,12), 120);
        films = service.filmsMap();
    }

    @AfterEach
    public void tearDown() {
        service.clearFilms();
    }

    @Test
    void addFilm() {
        int expectedSizeMap = 0;
        int actualSizeMap = films.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Map is not empty before start");

        film1 = service.addFilm(film1);

        expectedSizeMap = 1;
        actualSizeMap = films.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Collection size must be 1");

        film2 = service.addFilm(film2);

        expectedSizeMap = 2;
        actualSizeMap = films.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Collection size must be 2");

        assertTrue(films.containsValue(film1), "Film1 is missing from the collection");
        assertTrue(films.containsValue(film2), "Film2 is missing from the collection");
    }

    @Test
    void updateFilm() {
        int expectedSizeMap = 0;
        int actualSizeMap = films.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Map is not empty before start");
        service.addFilm(film1);

        expectedSizeMap = 1;
        actualSizeMap = films.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Collection size must be 1");

        Film actualFilm = new Film(
                1,
                "upName",
                "upDesc", LocalDate.of(2000, 1, 1),
                100
        );

        Film expectedFilm = service.updateFilm(actualFilm);

        assertEquals(actualFilm, expectedFilm, "Film hasn't been updated.");
    }

    @Test
    void getFilms() {
        List<Film> filmsList = service.getFilms();
        int expectedSizeList = 0;
        int actualSizeList = filmsList.size();
        assertEquals(expectedSizeList, actualSizeList, "List is not empty before start");

        Film expectedFilm1 = service.addFilm(film1);
        Film expectedFilm2 = service.addFilm(film2);

        filmsList = service.getFilms();
        expectedSizeList = 2;
        actualSizeList = filmsList.size();
        assertEquals(expectedSizeList, actualSizeList, "List size should be 2");

        assertTrue(filmsList.contains(expectedFilm1), "Film1 not listed");
        assertTrue(filmsList.contains(expectedFilm2), "Film2 not listed");
    }
}