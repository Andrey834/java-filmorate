package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DirectorControllerTest {
    private Film film1WithDirector;
    private Film film2WithoutDirector;
    private Film film3WithDirector;
    private Director director1;
    private Director director2;
    private Director director3;
    private User user1;
    private User user2;
    private Director notExistingDirector;
    private Director failNameDirector;
    private Director longNameDirector;
    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;
    @Autowired
    private DirectorController directorController;
    @Autowired
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        director1 = new Director(0, "James Cameron");
        director2 = new Director(0, "Steven Spielberg");
        director3 = new Director(0, "Peter Jackson");
        notExistingDirector = new Director(999, "John Lane");
        failNameDirector = new Director(0, "");
        longNameDirector = new Director(0, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit," +
                " sed diam nonummy nibh euismod tincidunt ut ");
        film1WithDirector = new Film(
                0,
                "HappyThreeFriends 1",
                "animated flash series about the adventures of several animals",
                LocalDate.of(1999, 11, 11),
                90,
                new Mpa(1, "R", "Best series")
        );
        film2WithoutDirector = new Film(
                0,
                "HappyThreeFriends 2",
                "Lisa bought a gun",
                LocalDate.of(2010, 12, 12),
                100,
                new Mpa(1, "R", "Best series")
        );
        film3WithDirector = new Film(
                0,
                "HappyThreeFriends 3",
                "Lisa bought a minigun",
                LocalDate.of(2005, 12, 12),
                100,
                new Mpa(1, "R", "Best series")
        );
        user1 = new User(
                0,
                "donutlover@gmail.com",
                "SuperJavaProgrammer2000",
                "Homer",
                LocalDate.of(1993, 11, 15)
        );

        user2 = new User(
                0,
                "snake@gmail.com",
                "angular777",
                "Liza",
                LocalDate.of(2000, 1, 1)
        );
    }

    @Test
    void createDirector() {
        Director director = directorController.createDirector(director1, request);
        List<Director> directors = directorController.getAllDirectors(request);
        int expectedDirectorsSize = 1;
        assertEquals(expectedDirectorsSize, directors.size(), "list must be " + expectedDirectorsSize);

        int expectedId = 1;
        int actualId = director.getId();
        assertEquals(expectedId, actualId, "directorId must be " + expectedId);

        Director newDirector = directorController.createDirector(director2, request);
        List<Director> newDirectors = directorController.getAllDirectors(request);
        int newExpectedDirectorsSize = 2;
        assertEquals(newExpectedDirectorsSize, newDirectors.size(), "list must be " + newExpectedDirectorsSize);

        int newExpectedId = 2;
        int newActualId = newDirector.getId();
        assertEquals(newExpectedId, newActualId, "directorId must be " + newExpectedId);
    }

    @Test
    void testBlankDirectorsName_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> directorController.createDirector(failNameDirector, request));
        assertEquals("Incorrect name", exception.getMessage());
    }

    @Test
    void testLongDirectorsName_ThrowsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> directorController.createDirector(longNameDirector, request));
        assertEquals("Incorrect name", exception.getMessage());
    }

    @Test
    void updateDirector() {
        Director director = directorController.createDirector(director1, request);
        int expectedId = 1;
        int actualId = director.getId();
        assertEquals(expectedId, actualId, "directorId must be " + expectedId);

        director.setName("Snoop Dogg");

        Director updatedDirector = directorController.updateDirector(director, request);
        int updatedExpectedId = 1;
        int updatedActualId = updatedDirector.getId();
        String expectedName = "Snoop Dogg";
        String actualName = updatedDirector.getName();
        assertEquals(updatedExpectedId, updatedActualId, "directorId must be " + expectedId);
        assertEquals(expectedName, actualName, "directorName must be " + expectedName);
    }

    @Test
    void updateNotExistingDirector_ThrowsNotFoundException() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> directorController.updateDirector(notExistingDirector, request));
        assertEquals(String.format("Режиссер с id=%s не найден", notExistingDirector.getId()), exception.getMessage());
    }

    @Test
    void deleteDirectorById() {
        directorController.createDirector(director1, request);
        directorController.createDirector(director2, request);

        List<Director> directors = directorController.getAllDirectors(request);
        int expectedDirectorsSize = 2;
        assertEquals(expectedDirectorsSize, directors.size(), "list must be " + expectedDirectorsSize);

        directorController.deleteDirectorById(1, request);

        List<Director> newDirectors = directorController.getAllDirectors(request);
        int newExpectedDirectorsSize = 1;
        assertEquals(newExpectedDirectorsSize, newDirectors.size(), "list must be " + newExpectedDirectorsSize);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> directorController.getDirectorById(1, request));
        assertEquals("Режиссер с id=1 не найден", exception.getMessage());
    }

    @Test
    void deleteAllDirectors() {
        directorController.createDirector(director1, request);
        directorController.createDirector(director2, request);

        List<Director> directors = directorController.getAllDirectors(request);
        int expectedDirectorsSize = 2;
        assertEquals(expectedDirectorsSize, directors.size(), "list must be " + expectedDirectorsSize);

        directorController.deleteAllDirectors(request);

        List<Director> newDirectors = directorController.getAllDirectors(request);
        int newExpectedDirectorsSize = 0;
        assertEquals(newExpectedDirectorsSize, newDirectors.size(), "list must be " + newExpectedDirectorsSize);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> directorController.getDirectorById(1, request));
        assertEquals("Режиссер с id=1 не найден", exception.getMessage());
    }

    @Test
    void createFilmWithDirector() {
        directorController.createDirector(director1, request);
        film1WithDirector.getDirectors().add(new Director(1, null));
        Film film = filmController.createFilm(film1WithDirector, request);
        int expectedFilmId = 1;
        int actualFilmId = film.getId();
        assertEquals(expectedFilmId, actualFilmId, "filmId must be " + expectedFilmId);

        Director director = film.getDirectors().stream().findFirst().get();
        int expectedDirectorId = 1;
        int actualDirectorId = director.getId();
        String expectedName = director1.getName();
        String actualName = director.getName();
        assertEquals(expectedDirectorId, actualDirectorId, "directorId must be " + expectedDirectorId);
        assertEquals(expectedName, actualName, "directorName must be " + expectedName);

        List<Director> directors = directorController.getAllDirectors(request);
        int expectedDirectorsSize = 1;
        assertEquals(expectedDirectorsSize, directors.size(), "list must be " + expectedDirectorsSize);
    }

    @Test
    void createFilmWithoutDirector() {
        directorController.createDirector(director1, request);
        Film film = filmController.createFilm(film2WithoutDirector, request);
        int expectedFilmId = 1;
        int actualFilmId = film.getId();
        assertEquals(expectedFilmId, actualFilmId, "filmId must be " + expectedFilmId);

        Set<Director> directorsSet = film.getDirectors();
        int expectedSetDirectorsSize = 0;
        int actualSetDirectorsSize = directorsSet.size();
        assertEquals(expectedSetDirectorsSize, actualSetDirectorsSize, "directorsSize must be " + expectedSetDirectorsSize);

        List<Director> directors = directorController.getAllDirectors(request);
        int expectedDirectorsSize = 1;
        assertEquals(expectedDirectorsSize, directors.size(), "list must be " + expectedDirectorsSize);
    }

    @Test
    void getFilmsForDirectorSortedByYear() {
        directorController.createDirector(director1, request);
        directorController.createDirector(director2, request);
        directorController.createDirector(director3, request);

        film1WithDirector.getDirectors().add(new Director(1, null));
        film3WithDirector.getDirectors().add(new Director(1, null));

        filmController.createFilm(film1WithDirector, request);
        filmController.createFilm(film2WithoutDirector, request);
        filmController.createFilm(film3WithDirector, request);

        List<Film> films = filmController.getAllDirectorFilmsSorted(1, "year", request);
        int expectedFilmsSize = 2;
        int actualFilmsSize = films.size();
        assertEquals(expectedFilmsSize, actualFilmsSize, "list must be " + expectedFilmsSize);

        List<Integer> expectedFilmIdOrder = List.of(1, 3);
        List<Integer> actualFilmOrder = films.stream().map(Film::getId).collect(Collectors.toList());
        assertEquals(expectedFilmIdOrder, actualFilmOrder, "order must be " + expectedFilmIdOrder);
    }

    @Test
    void getFilmsForDirectorSortedByLikes() {
        directorController.createDirector(director1, request);
        directorController.createDirector(director2, request);
        directorController.createDirector(director3, request);

        film1WithDirector.getDirectors().add(new Director(2, null));
        film3WithDirector.getDirectors().add(new Director(2, null));

        Film film1 = filmController.createFilm(film1WithDirector, request);
        Film film2 = filmController.createFilm(film2WithoutDirector, request);
        Film film3 = filmController.createFilm(film3WithDirector, request);

        userController.createUser(user1, request);
        userController.createUser(user2, request);

        filmController.plusLike(1, 1, request);
        filmController.plusLike(2, 2, request);
        filmController.plusLike(3, 1, request);
        filmController.plusLike(3, 2, request);

        List<Film> films = filmController.getAllDirectorFilmsSorted(2, "likes", request);
        int expectedFilmsSize = 2;
        int actualFilmsSize = films.size();
        assertEquals(expectedFilmsSize, actualFilmsSize, "list must be " + expectedFilmsSize);

        List<Integer> expectedFilmIdOrder = List.of(3, 1);
        List<Integer> actualFilmOrder = films.stream().map(Film::getId).collect(Collectors.toList());
        assertEquals(expectedFilmIdOrder, actualFilmOrder, "order must be " + expectedFilmIdOrder);
    }
}