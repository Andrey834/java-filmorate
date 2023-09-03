package ru.yandex.practicum.filmorate.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
    private User testUser;
    private Film testFilm;
    @Autowired
    private UserController userController;
    @Autowired
    private FilmController filmController;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockHttpServletRequest request;


    @BeforeEach
    void createUser() {
        testUser = new User(
                1,
                "donutlover@gmail.com",
                "SuperJavaProgrammer2000",
                "Homer",
                LocalDate.of(1993, 11, 15)
        );
        testFilm = new Film(
                1,
                "HappyThreeFriends",
                "animated flash series about the adventures of several animals",
                LocalDate.of(1999, 12, 24),
                5,
                new Mpa(1, "R", "Best series")
        );
    }

    @Test
    void testBlankEmail_ThrowsValidationException() {
        testUser.setEmail("");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(testUser, request)
        );

        assertEquals("Incorrect email", exception.getMessage());
    }

    @Test
    void testEmailContainsSymbol_ThrowsValidationException() {
        testUser.setEmail("memelover@gmail.com@");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(testUser, request)
        );

        assertEquals("Incorrect email", exception.getMessage());
    }

    @Test
    void testBlankLogin_ThrowsValidationException() {
        testUser.setLogin("");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(testUser, request)
        );

        assertEquals("Incorrect login", exception.getMessage());
    }

    @Test
    void testLoginContainsSpaces_ThrowsValidationException() {
        testUser.setLogin("Super JavaProgrammer2000");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(testUser, request)
        );

        assertEquals("Incorrect login", exception.getMessage());
    }

    @Test
    void testBirthdayDateAfterCurrentDate_ThrowsValidationException() {
        testUser.setBirthday(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(testUser, request)
        );

        assertEquals("Date of birth cannot be in future", exception.getMessage());
    }

    @Test
    void testBlankName_ThrowsValidationException() {
        testUser.setName("");
        userController.createUser(testUser, request);
        assertEquals(testUser.getName(), testUser.getLogin());
    }

    @Test
    void testIncorrectId_ThrowsValidationException() {
        testUser.setId(-1);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userController.updateUser(testUser, request)
        );

        assertEquals("User was not found.", exception.getMessage());
    }

    @Test
    void testGetUsers_ReturnsAllUsers() {
        User testUser2 = new User(
                2,
                "beerlover@yandex.ru",
                "BadJavaProgrammer",
                "Barney Gumble",
                LocalDate.of(1989, 4, 20));

        Map<Integer, User> users = new HashMap<>();
        users.put(1, testUser);
        users.put(2, testUser2);

        userController.createUser(testUser, request);
        userController.createUser(testUser2, request);

        Collection<User> result = userController.getUsers(request);
        assertTrue(result.containsAll(users.values()));
    }

    @Test
    void testUserFriends() {
        User friend = userController.createUser(new User(
                2,
                "beerlover@yandex.ru",
                "BadJavaProgrammer",
                "Barney Gumble",
                LocalDate.of(1989, 4, 20)), request
        );

        User notFriend = userController.createUser(new User(
                3,
                "beerlover@yandex.ru",
                "BadJavaProgrammer",
                "Barney Gumble",
                LocalDate.of(1989, 4, 20)), request
        );

        User user = userController.createUser(testUser, request);
        userController.plusFriend(user.getId(), friend.getId(), request);

        User userWithFriend = userController.getUserById(user.getId(), request);

        assertFalse(userWithFriend.getFriends().contains(notFriend.getId()));
        assertTrue(userWithFriend.getFriends().contains(friend.getId()));
        assertEquals(notFriend.getFriends().size(), 0);
    }

    @Test
    void removeFriendTest() {
        User friend = userController.createUser(new User(
                2,
                "beerlover@yandex.ru",
                "BadJavaProgrammer",
                "Barney Gumble",
                LocalDate.of(1989, 4, 20)), request
        );

        User user = userController.createUser(testUser, request);
        userController.plusFriend(user.getId(), friend.getId(), request);
        userController.minusFriend(user.getId(), friend.getId(), request);

        assertFalse(user.getFriends().contains(friend.getId()));
        assertFalse(friend.getFriends().contains(user.getId()));
        assertEquals(friend.getFriends().size(), 0);
    }

    @Test
    void testMutualFriends() {
        User user = userController.createUser(testUser, request);
        User friend = userController.createUser(new User(
                2,
                "beerlover@yandex.ru",
                "BadJavaProgrammer",
                "Barney Gumble",
                LocalDate.of(1989, 4, 20)), request
        );

        User mutualFriend = userController.createUser(new User(
                3,
                "friendForAll@yandex.ru",
                "justAFriendlyGuy",
                "Doctor Aibolit",
                LocalDate.of(1960, 4, 11)), request
        );


        assertEquals(userController.getMutualFriends(user.getId(), friend.getId(), request).size(), 0);
        assertEquals(userController.getMutualFriends(friend.getId(), mutualFriend.getId(), request).size(), 0);

        userController.plusFriend(user.getId(), mutualFriend.getId(), request);
        userController.plusFriend(user.getId(), friend.getId(), request);
        userController.plusFriend(mutualFriend.getId(), user.getId(), request);
        userController.plusFriend(friend.getId(), mutualFriend.getId(), request);
        userController.plusFriend(friend.getId(), user.getId(), request);

        user = userController.getUserById(user.getId(), request);
        friend = userController.getUserById(friend.getId(), request);
        mutualFriend = userController.getUserById(mutualFriend.getId(), request);

        assertEquals(userController.getMutualFriends(user.getId(), friend.getId(), request).size(), 1);
        assertEquals(userController.getMutualFriends(friend.getId(), mutualFriend.getId(), request).size(), 1);

        assertFalse(userController.getMutualFriends(user.getId(), friend.getId(), request).isEmpty());
        assertTrue(userController.getMutualFriends(user.getId(), mutualFriend.getId(), request).isEmpty());
        assertFalse(userController.getMutualFriends(friend.getId(), mutualFriend.getId(), request).isEmpty());
    }

    @Test
    void userFriendReturnTest() {
        User user = userController.createUser(testUser, request);
        User friend = userController.createUser(new User(
                2,
                "beerlover@yandex.ru",
                "BadJavaProgrammer",
                "Barney Gumble",
                LocalDate.of(1989, 4, 20)), request
        );

        userController.plusFriend(user.getId(), friend.getId(), request);
        User user2 = userController.getUserById(user.getId(), request);

        assertTrue(user2.getFriends().contains(friend.getId()));
        assertEquals(user2.getFriends().size(), 1);
    }

    @Test
    void testGetUserById() {
        User user1 = userController.createUser(testUser, request);
        User user2 = userController.createUser(testUser, request);
        userController.getUserById(user2.getId(), request);

        assertEquals(2, user2.getId());
    }

    @Test
    public void testDeleteUser() {
        User user = userController.createUser(testUser, request);
        int actualSizeList = userController.getUsers(request).size();
        assertEquals(1, actualSizeList);

        userController.deleteUser(user.getId(), request);
        actualSizeList = userController.getUsers(request).size();

        assertEquals(0, actualSizeList);
    }

    @Test
    void testGetRecommendationsByExistingUserId() {
        User testUser2 = new User(2, "homerlover@gmail.com",
                "SuperWifeOfJavaProgrammer2000",
                "Marge",
                LocalDate.of(1993, 11, 16));
        Film testFilm2 = new Film(2,
                "Green Elephant",
                "Very kind and educational movie for children",
                LocalDate.of(1999, 8, 24),
                86,
                new Mpa(1, "G", "All ages admitted"));
        User user1 = userController.createUser(testUser, request);
        User user2 = userController.createUser(testUser2, request);
        Film film1 = filmController.createFilm(testFilm, request);
        Film film2 = filmController.createFilm(testFilm2, request);
        filmController.plusLike(film1.getId(), user1.getId(), request);
        filmController.plusLike(film1.getId(), user2.getId(), request);
        filmController.plusLike(film2.getId(), user2.getId(), request);
        Film film1AfterLikes = filmController.getFilmById(1, request);
        Film film2AfterLikes = filmController.getFilmById(2, request);

        assertEquals(2, film1AfterLikes.getLikes().size());
        assertEquals(1, film2AfterLikes.getLikes().size());

        List<Film> user1Recommendations = userController.getRecommendations(1, request);

        assertEquals(1, user1Recommendations.size());
        assertTrue(user1Recommendations.contains(film2AfterLikes));
    }

    @Test
    void testGetRecommendationsByNonExistingUserId() {
        NotFoundException ex = assertThrows(
                NotFoundException.class, () -> userController.getRecommendations(999, request));

        assertEquals("User was not found.", ex.getMessage());
    }

    @Test
    void testGetRecommendationsWithNoLikes() {
        User testUser2 = new User(2, "homerlover@gmail.com",
                "SuperWifeOfJavaProgrammer2000",
                "Marge",
                LocalDate.of(1993, 11, 16));
        User user1 = userController.createUser(testUser, request);
        User user2 = userController.createUser(testUser2, request);
        List<Film> user1Recommendations = userController.getRecommendations(1, request);

        assertEquals(0, user1Recommendations.size());
    }

    @Test
    void testGetRecommendationsWithNoCommonLikes() {
        User testUser2 = new User(2, "homerlover@gmail.com",
                "SuperWifeOfJavaProgrammer2000",
                "Marge",
                LocalDate.of(1993, 11, 16));
        Film testFilm2 = new Film(2,
                "Green Elephant",
                "Very kind and educational movie for children",
                LocalDate.of(1999, 8, 24),
                86,
                new Mpa(1, "G", "All ages admitted"));
        User user1 = userController.createUser(testUser, request);
        User user2 = userController.createUser(testUser2, request);
        Film film1 = filmController.createFilm(testFilm, request);
        Film film2 = filmController.createFilm(testFilm2, request);
        filmController.plusLike(film1.getId(), user1.getId(), request);
        filmController.plusLike(film2.getId(), user2.getId(), request);
        Film film1AfterLikes = filmController.getFilmById(1, request);
        Film film2AfterLikes = filmController.getFilmById(2, request);

        assertEquals(1, film1AfterLikes.getLikes().size());
        assertEquals(1, film2AfterLikes.getLikes().size());

        List<Film> user1Recommendations = userController.getRecommendations(1, request);

        assertEquals(0, user1Recommendations.size());
    }

    @Test
    void testGetEvents() {
        User testUser2 = new User(2, "homerlover@gmail.com",
                "SuperWifeOfJavaProgrammer2000",
                "Marge",
                LocalDate.of(1993, 11, 16));

        User user1 = userController.createUser(testUser, request);
        User user2 = userController.createUser(testUser2, request);

        List<Event> events = userController.getUserEvents(user1.getId(), request);

        int expectedLiseSize = 0;
        int actualListSize = events.size();
        assertEquals(expectedLiseSize, actualListSize);

        userController.plusFriend(user1.getId(), user2.getId(), request);

        events = userController.getUserEvents(user1.getId(), request);

        expectedLiseSize = 1;
        actualListSize = events.size();
        assertEquals(expectedLiseSize, actualListSize);

        Event event = events.get(0);

        int expectedUserId = 1;
        int expectedEventId = 1;
        EventType expectedEventType = EventType.FRIEND;
        Operation expectedOperation = Operation.ADD;

        assertEquals(expectedUserId, event.getUserId());
        assertEquals(expectedEventId, event.getEventId());
        assertEquals(expectedEventType, event.getEventType());
        assertEquals(expectedOperation, event.getOperation());
    }
}