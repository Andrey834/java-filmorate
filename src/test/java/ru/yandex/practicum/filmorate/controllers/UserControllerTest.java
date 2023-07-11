package ru.yandex.practicum.filmorate.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class UserControllerTest {
    private User testUser;
    @Autowired
    private UserController userController;
    @Autowired
    private MockHttpServletRequest request;


    @BeforeEach
    public void createUser() {
        testUser = new User(
                1,
                "donutlover@gmail.com",
                "SuperJavaProgrammer2000",
                "Homer",
                LocalDate.of(1993, 11, 15));
    }

    @Test
    public void testBlankEmail_ThrowsValidationException() {
        testUser.setEmail("");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(testUser, request));
        assertEquals("Incorrect email", exception.getMessage());
    }

    @Test
    public void testEmailContainsSymbol_ThrowsValidationException() {
        testUser.setEmail("memelover@gmail.com@");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(testUser, request));
        assertEquals("Incorrect email", exception.getMessage());
    }

    @Test
    public void testBlankLogin_ThrowsValidationException() {
        testUser.setLogin("");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(testUser, request));
        assertEquals("Incorrect login", exception.getMessage());
    }

    @Test
    public void testLoginContainsSpaces_ThrowsValidationException() {
        testUser.setLogin("Super JavaProgrammer2000");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(testUser, request));
        assertEquals("Incorrect login", exception.getMessage());
    }

    @Test
    public void testBirthdayDateAfterCurrentDate_ThrowsValidationException() {
        testUser.setBirthday(LocalDate.now().plusDays(1));
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(testUser, request));
        assertEquals("Date of birth cannot be in future", exception.getMessage());
    }

    @Test
    public void testBlankName_ThrowsValidationException() {
        testUser.setName("");
        userController.createUser(testUser, request);
        assertEquals(testUser.getName(), testUser.getLogin());
    }

    @Test
    public void testIncorrectId_ThrowsValidationException() {
        testUser.setId(-1);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.updateUser(testUser, request));
        assertEquals("404. User not found.", exception.getMessage());
    }

    @Test
    public void testGetUsers_ReturnsAllUsers() {
        User testUser2 = userController.createUser(new User(
                2,
                "beerlover@yandex.ru",
                "BadJavaProgrammer",
                "Barney Gumble",
                LocalDate.of(1989, 4, 20)), request);

        Map<Integer, User> users = new HashMap<>();
        users.put(1, testUser);
        users.put(2, testUser2);

        userController.createUser(testUser, request);
        userController.createUser(testUser2, request);

        Collection<User> result = userController.getUsers(request);
        assertTrue(result.containsAll(users.values()));
    }

    @Test
    public void testUserFriends() {
        User friend = userController.createUser(new User(
                2,
                "beerlover@yandex.ru",
                "BadJavaProgrammer",
                "Barney Gumble",
                LocalDate.of(1989, 4, 20)), request);

        User notFriend = userController.createUser(new User(
                3,
                "beerlover@yandex.ru",
                "BadJavaProgrammer",
                "Barney Gumble",
                LocalDate.of(1989, 4, 20)), request);

        User user = userController.createUser(testUser, request);
        userController.plusFriend(user.getId(), friend.getId(), request);

        assertFalse(user.getFriends().contains(notFriend.getId()));
        assertTrue(user.getFriends().contains(friend.getId()));
        assertEquals(notFriend.getFriends().size(), 0);
    }

    @Test
    void removeFriendTest() {
        User friend = userController.createUser(new User(
                2,
                "beerlover@yandex.ru",
                "BadJavaProgrammer",
                "Barney Gumble",
                LocalDate.of(1989, 4, 20)), request);

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
                LocalDate.of(1989, 4, 20)), request);

        User mutualFriend = userController.createUser(new User(
                3,
                "friendForAll@yandex.ru",
                "justAFriendlyGuy",
                "Doctor Aibolit",
                LocalDate.of(1960, 4, 11)), request);

        assertEquals(userController.getMutualFriends(user.getId(), friend.getId(), request).size(), 0);
        assertEquals(userController.getMutualFriends(friend.getId(), mutualFriend.getId(), request).size(), 0);

        userController.plusFriend(user.getId(), mutualFriend.getId(), request);
        userController.plusFriend(user.getId(), friend.getId(), request);
        userController.plusFriend(friend.getId(), mutualFriend.getId(), request);

        assertEquals(userController.getMutualFriends(user.getId(), friend.getId(), request).size(), 1);
        assertEquals(userController.getMutualFriends(friend.getId(), mutualFriend.getId(), request).size(), 1);

        assertFalse(userController.getMutualFriends(user.getId(), friend.getId(), request).isEmpty());
        assertFalse(userController.getMutualFriends(user.getId(), mutualFriend.getId(), request).isEmpty());
        assertFalse(userController.getMutualFriends(friend.getId(), mutualFriend.getId(), request).isEmpty());
    }

    @Test
    public void testGetFilmById() {
        User user1 = userController.createUser(testUser, request);
        User user2 = userController.createUser(testUser, request);
        userController.getUserById(user2.getId(), request);

        assertEquals(2, user2.getId());
    }
}