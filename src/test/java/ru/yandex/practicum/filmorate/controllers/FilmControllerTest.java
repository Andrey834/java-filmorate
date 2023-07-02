package ru.yandex.practicum.filmorate.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class FilmControllerTest {
    private Film testFilm;
    FilmController test = new FilmController();
    MockHttpServletRequest request = new MockHttpServletRequest();

    @BeforeEach
    public void createFilm() {
        testFilm = new Film(
                1,
                "HappyThreeFriends",
                "animated flash series about the adventures of several animals",
                LocalDate.of(1999, 12, 24),
                5
        );
    }

    @Test
    public void BlankEmailTest_ThrowsValidationException() {
        testFilm.setName("");
        ValidationException exception = assertThrows(ValidationException.class, () -> test.createFilm(testFilm, request));
        assertEquals("Incorrect name", exception.getMessage());
    }

    @Test
    public void DescriptionBigLengthTest_ThrowsValidationException() {
        testFilm.setDescription("This is a cartoon about small cute animals with which various adventures constantly" +
                " happen, in which something goes wrong all the time. And no matter how innocently their day began -" +
                "- at the end of it there will be a real mayhem. Traveling through their picturesque world and talking" +
                " in gibberish is all they have, and most importantly, they never know what troubles await them" +
                " in the next second.");
        ValidationException exception = assertThrows(ValidationException.class, () -> test.createFilm(testFilm, request));
        assertEquals("Incorrect length", exception.getMessage());
    }

    @Test
    public void DurationLessThan0Test_ThrowsValidationException() {
        testFilm.setDuration(-1);
        ValidationException exception = assertThrows(ValidationException.class, () -> test.createFilm(testFilm, request));
        assertEquals("Incorrect duration", exception.getMessage());
    }

    @Test
    public void IncorrectReleaseDateTest_ThrowsValidationException() {
        testFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        ValidationException exception = assertThrows(ValidationException.class, () -> test.createFilm(testFilm, request));
        assertEquals("Incorrect release date", exception.getMessage());
    }

    @Test
    public void IncorrectIdTest_ThrowsValidationException() {
        testFilm.setId(-1);
        ValidationException exception = assertThrows(ValidationException.class, () -> test.updateFilm(testFilm, request));
        assertEquals("Incorrect id", exception.getMessage());
    }

    @Test
    public void GetFilmsTest_ReturnsAllFilms() {
        Film testFilm2 = new Film(
                1,
                "Simpsons",
                "animated comedian series",
                LocalDate.of(1989, 12, 17),
                30);

        Map<Integer, Film> users = new HashMap<>();
        users.put(1, testFilm);
        users.put(2, testFilm2);

        test.createFilm(testFilm, request);
        test.createFilm(testFilm2, request);

        Collection<Film> result = test.getFilms();
        assertTrue(result.containsAll(users.values()));
    }
}