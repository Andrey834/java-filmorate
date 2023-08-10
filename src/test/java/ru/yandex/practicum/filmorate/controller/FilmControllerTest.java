package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.*;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor
class FilmControllerTest {
    private Film testFilm;
    private User testuser;

    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockHttpServletRequest request;



    @BeforeEach
    public void createFilm() {
        testFilm = new Film(
                1,
                "HappyThreeFriends",
                "animated flash series about the adventures of several animals",
                LocalDate.of(1999, 12, 24),
                5,
                new Mpa(1,"R", "Best series")
        );
        testuser = new User(
                1,
                "JavaProgrammer2000@yandex.ru",
                "Qwerty",
                "Afanasiy",
                LocalDate.of(1993, 11, 15));
    }

    @Test
    public void testBlankEmail_ThrowsValidationException() {
        testFilm.setName("");
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(testFilm, request));
        assertEquals("Incorrect name", exception.getMessage());
    }

    @Test
    public void testDescriptionBigLength_ThrowsValidationException() {
        testFilm.setDescription("This is a cartoon about small cute animals with which various adventures constantly" +
                " happen, in which something goes wrong all the time. And no matter how innocently their day began -" +
                "- at the end of it there will be a real mayhem. Traveling through their picturesque world and talking" +
                " in gibberish is all they have, and most importantly, they never know what troubles await them" +
                " in the next second.");
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(testFilm, request));
        assertEquals("Incorrect length", exception.getMessage());
    }

    @Test
    public void testDurationLessThan0_ThrowsValidationException() {
        testFilm.setDuration(-1);
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(testFilm, request));
        assertEquals("Incorrect duration", exception.getMessage());
    }

    @Test
    public void testIncorrectReleaseDate_ThrowsValidationException() {
        testFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(testFilm, request));
        assertEquals("Incorrect release date", exception.getMessage());
    }

    @Test
    public void testIncorrectId_ThrowsValidationException() {
        testFilm.setId(-1);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmController.updateFilm(testFilm, request));
        assertEquals("Film was not found.", exception.getMessage());
    }

    @Test
    public void testGetFilms_ReturnsAllFilms() {
        Film testFilm2 = new Film(
                1,
                "Simpsons",
                "animated comedian series",
                LocalDate.of(1989, 12, 17),
                30,
                new Mpa(1, "G", "Simpsons forever")
        );

        Map<Integer, Film> users = new HashMap<>();
        users.put(1, testFilm);
        users.put(2, testFilm2);

        filmController.createFilm(testFilm, request);
        filmController.createFilm(testFilm2, request);

        Collection<Film> result = filmController.getFilms(request);
        assertTrue(result.containsAll(users.values()));
    }

    @Test
    public void default0LikesTest() {
        Film film = filmController.createFilm(testFilm, request);

        assertNotNull(film.getLikes());
        assertEquals(film.getLikes().size(), 0);
    }

    @Test
    public void testPlusLike() {
        Film film = filmController.createFilm(testFilm, request);
        User user = userController.createUser(testuser, request);
        filmController.plusLike(film.getId(), testuser.getId(), request);

        assertTrue(film.getLikes().contains(testuser.getId()));
        assertEquals(film.getLikes().size(), 1);
    }

    @Test
    public void testMinusLike() {
        Film film = filmController.createFilm(testFilm, request);
        User user = userController.createUser(testuser, request);
        filmController.plusLike(film.getId(), user.getId(), request);
        filmController.minusLike(film.getId(), user.getId(), request);

        assertEquals(film.getLikes().size(), 0);
    }

    @Test
    public void testGetMostPopularFilms() {
        Film film1 = filmController.createFilm(testFilm, request);
        Film film2 = filmController.createFilm(testFilm, request);
        Film film3 = filmController.createFilm(testFilm, request);
        User user = userController.createUser(testuser, request);
        filmController.plusLike(film2.getId(), testuser.getId(), request);
        List<Film> films = filmController.getMostPopularFilms(1, request);

        assertEquals(1, films.size());
    }

    @Test
    public void testGetFilmById() {
        Film film1 = filmController.createFilm(testFilm, request);
        Film film2 = filmController.createFilm(testFilm, request);
        filmController.getFilmById(film2.getId(), request);

        assertEquals(2, film2.getId());
    }
}