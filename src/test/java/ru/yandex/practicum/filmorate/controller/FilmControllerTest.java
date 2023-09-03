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
import ru.yandex.practicum.filmorate.exception.EmptyObjectException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor
class FilmControllerTest {
    private Film testFilm;
    private Film testFilm2;
    private User testUser;
    private User testUser2;
    private User testUser3;
    private Director director;

    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;
    @Autowired
    private DirectorController directorController;
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
                new Mpa(1, "R", "Best series")
        );

        testFilm2 = new Film(
                2,
                "BadStory",
                "description film2",
                LocalDate.of(2000, 11, 11),
                5,
                new Mpa(1, "R", "Best series")
        );

        director = new Director(0, "James Cameron");

        testUser = new User(
                1,
                "JavaProgrammer2000@yandex.ru",
                "Qwerty",
                "Afanasiy",
                LocalDate.of(1993, 11, 15));

        testUser2 = new User(
                2,
                "hoolia@yandex.ru",
                "Ajax",
                "John",
                LocalDate.of(2001, 1, 1));

        testUser3 = new User(
                3,
                "dsd@yandex.ru",
                "Guns",
                "Max",
                LocalDate.of(1987, 1, 1));
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
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmController.updateFilm(testFilm, request)
        );
        assertEquals("Film with id=" + testFilm.getId() + " was not found.", exception.getMessage());
    }

    @Test
    public void testGetFilms_ReturnsAllFilms() {
        Film testFilm2 = new Film(
                1,
                "Simpsons",
                "animated comedian series",
                LocalDate.of(1989, 12, 17),
                30,
                new Mpa(2, "G", "Simpsons forever")
        );

        filmController.createFilm(testFilm, request);
        filmController.createFilm(testFilm2, request);

        Film testVal1 = filmController.getFilmById(testFilm.getId(), request);
        Film testVal2 = filmController.getFilmById(testFilm2.getId(), request);

        Collection<Film> testCol = new ArrayList<>();
        testCol.add(testVal1);
        testCol.add(testVal2);

        Collection<Film> result = filmController.getFilms(request);
        assertEquals(testCol, result);
    }

    @Test
    void longDescriptionExceptionTest() {
        testFilm.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor" +
                                " incididunt ut labore et dolore magna aliqua. In fermentum posuere urna nec tincidunt. Suspendisse" +
                                " sed nisi lacus sed. Sed turpis tincidunt id aliquet risus. Egestas sed tempus urna et. Malesuada" +
                                " bibendum arcu vitae elementum. Erat nam at lectus urna duis convallis convallis tellus id." +
                                " Mus mauris vitae ultricies leo integer. Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                                " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. In fermentum posuere urna nec" +
                                " tincidunt. Suspendisse sed nisi lacus sed. Sed turpis tincidunt id aliquet risus. Egestas sed" +
                                " tempus urna et. Malesuada bibendum arcu vitae elementum. Erat nam at lectus urna duis convallis" +
                                " convallis tellus id. Mus mauris vitae ultricies leo integer." +
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor" +
                                " incididunt ut labore et dolore magna aliqua. In fermentum posuere urna nec tincidunt. Suspendisse" +
                                " sed nisi lacus sed. Sed turpis tincidunt id aliquet risus. Egestas sed tempus urna et. Malesuada" +
                                " bibendum arcu vitae elementum. Erat nam at lectus urna duis convallis convallis tellus id." +
                                " Mus mauris vitae ultricies leo integer. Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                                " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. In fermentum posuere urna nec" +
                                " tincidunt. Suspendisse sed nisi lacus sed. Sed turpis tincidunt id aliquet risus. Egestas sed tempus" +
                                " urna et. Malesuada bibendum arcu vitae elementum. Erat nam at lectus urna duis convallis" +
                                " convallis tellus id. Mus mauris vitae ultricies leo integer."
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(testFilm, request)
        );
        assertEquals("Incorrect length", exception.getMessage());
    }

    @Test
    public void default0LikesTest() {
        Film film = filmController.createFilm(testFilm, request);

        assertNotNull(film.getLikes());
        assertEquals(film.getLikes().size(), 0);
    }

    @Test
    public void testPlusLike() {
        Film filmBeforeLike = filmController.createFilm(testFilm, request);
        User user = userController.createUser(testUser, request);
        filmController.plusLike(filmBeforeLike.getId(), user.getId(), request);

        Film filmAfterLike = filmController.getFilmById(filmBeforeLike.getId(), request);
        assertTrue(filmAfterLike.getLikes().contains(user.getId()));
        assertEquals(filmAfterLike.getLikes().size(), 1);
    }

    @Test
    public void testMinusLike() {
        Film film = filmController.createFilm(testFilm, request);
        User user = userController.createUser(testUser, request);
        filmController.plusLike(film.getId(), user.getId(), request);

        Film filmAfterLike = filmController.getFilmById(film.getId(), request);
        assertTrue(filmAfterLike.getLikes().contains(user.getId()));
        assertEquals(filmAfterLike.getLikes().size(), 1);

        filmController.minusLike(filmAfterLike.getId(), user.getId(), request);
        Film filmWithoutLike = filmController.getFilmById(filmAfterLike.getId(), request);
        assertFalse(filmWithoutLike.getLikes().contains(user.getId()));
        assertEquals(filmWithoutLike.getLikes().size(), 0);
    }

    @Test
    public void testTopFilms() {
        filmController.createFilm(testFilm, request);
        Film film2 = filmController.createFilm(testFilm2, request);

        userController.createUser(testUser, request);
        filmController.plusLike(film2.getId(), testUser.getId(), request);
        List<Film> films = filmController.getTopFilms(1, 0, 0, request);

        assertEquals(1, films.size());
    }

    @Test
    void negativeDurationExceptionTest() {
        testFilm.setDuration(-1);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(testFilm, request)
        );
        assertEquals("Incorrect duration", exception.getMessage());
    }

    @Test
    void emptyNameExceptionTest() {
        testFilm.setName("");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.createFilm(testFilm, request)
        );
        assertEquals("Incorrect name", exception.getMessage());
    }

    @Test
    void nullExceptionTest() {
        EmptyObjectException exception = assertThrows(
                EmptyObjectException.class,
                () -> filmController.createFilm(null, request)
        );
        assertEquals("Film was not provided.", exception.getMessage());
    }

    @Test
    public void testGetFilmById() {
        filmController.createFilm(testFilm, request);
        Film film2 = filmController.createFilm(testFilm, request);
        filmController.getFilmById(film2.getId(), request);

        assertEquals(2, film2.getId());
    }

    @Test
    public void testGetJointFilms() {
        Film film1 = filmController.createFilm(testFilm, request);
        Film film2 = filmController.createFilm(testFilm2, request);
        User user1 = userController.createUser(testUser, request);
        User user2 = userController.createUser(testUser2, request);
        User user3 = userController.createUser(testUser3, request);

        int film1Id = film1.getId();
        int film2Id = film2.getId();

        int user1Id = user1.getId();
        int user2Id = user2.getId();
        int user3Id = user3.getId();

        filmController.plusLike(film1Id, user1Id, request);
        filmController.plusLike(film1Id, user2Id, request);
        filmController.plusLike(film2Id, user1Id, request);
        filmController.plusLike(film2Id, user2Id, request);
        filmController.plusLike(film2Id, user3Id, request);

        List<Film> jointFilms = filmController.jointFilms(user1Id, user2Id, request);

        assertTrue(jointFilms.contains(film1));

        Film actualFirstFilm = jointFilms.get(0);

        assertEquals(film2, actualFirstFilm, "First place must be film with ID" + film2Id);

        int expectedSizeList = 2;
        int actualSizeList = jointFilms.size();

        assertEquals(expectedSizeList, actualSizeList, "Size list must be " + expectedSizeList);
    }

    @Test
    public void testDeleteFilm() {
        Film film = filmController.createFilm(testFilm, request);
        int actualSizeList = filmController.getFilms(request).size();
        assertEquals(1, actualSizeList);

        filmController.deleteFilm(film.getId(), request);
        actualSizeList = filmController.getFilms(request).size();
        assertEquals(0, actualSizeList);
    }

    @Test
    public void testSearchByFilmName() {
        Film film = filmController.createFilm(testFilm, request);
        filmController.createFilm(testFilm2, request);

        List<Film> searchFilm = filmController.getSearchFilms("Three", "title", request);

        assertEquals(1, searchFilm.size());
        assertEquals(List.of(film), searchFilm);
    }

    @Test
    public void testSearchByFilmDirector() {
        directorController.createDirector(director, request);
        testFilm.getDirectors().add(new Director(1, null));
        Film film = filmController.createFilm(testFilm, request);
        filmController.createFilm(testFilm2, request);

        List<Film> searchFilm = filmController.getSearchFilms("ron", "director", request);

        assertEquals(1, searchFilm.size());
        assertEquals(List.of(film), searchFilm);
    }

    @Test
    public void testSearchByFilmNameOrDirector() {
        directorController.createDirector(director, request);
        testFilm.getDirectors().add(new Director(1, null));
        Film film = filmController.createFilm(testFilm, request);
        filmController.createFilm(testFilm2, request);

        List<Film> searchFilm = filmController.getSearchFilms("ron", "title,director", request);

        assertEquals(1, searchFilm.size());
        assertEquals(List.of(film), searchFilm);
    }
}