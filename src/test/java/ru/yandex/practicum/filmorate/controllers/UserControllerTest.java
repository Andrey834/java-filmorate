package ru.yandex.practicum.filmorate.controllers;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class UserControllerTest {
    private User testUser;
    UserController test = new UserController();
    MockHttpServletRequest request = new MockHttpServletRequest();

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
    public void testCreateUser_BlankEmail_ThrowsValidationException() {
        testUser.setEmail("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            test.createUser(testUser, request);
        });
        assertEquals("Incorrect email", exception.getMessage());
    }

    @Test
    public void testCreateUser_EmailContainsSymbol_ThrowsValidationException() {
        testUser.setEmail("memelover@gmail.com@");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            test.createUser(testUser, request);
        });
        assertEquals("Incorrect email", exception.getMessage());
    }

    @Test
    public void testCreateUser_BlankLogin_ThrowsValidationException() {
        testUser.setLogin("");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            test.createUser(testUser, request);
        });
        assertEquals("Incorrect login", exception.getMessage());
    }

    @Test
    public void testCreateUser_LoginContainsSpaces_ThrowsValidationException() {
        testUser.setLogin("Super JavaProgrammer2000");
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            test.createUser(testUser, request);
        });
        assertEquals("Incorrect login", exception.getMessage());
    }

    @Test
    public void testCreateUser_BirthdayDateAfterCurrentDate_ThrowsValidationException() {
        testUser.setBirthday(LocalDate.now().plusDays(1));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            test.createUser(testUser, request);
        });
        assertEquals("Date of birth cannot be in future", exception.getMessage());
    }

    @Test
    public void testCreateUser_BlankName_ThrowsValidationException() {
        testUser.setName("");
        test.createUser(testUser, request);
        assertEquals(testUser.getName(), testUser.getLogin());
    }

    @Test
    public void testCreateUser_IncorrectId_ThrowsValidationException() {
        testUser.setId(-1);
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            test.createUser(testUser, request);
        });
        assertEquals("Incorrect Id", exception.getMessage());
    }

    @Test
    public void testGetUsers_ReturnsAllUsers() {
        User testUser2 = new User(
                2,
                "beerlover@yandex.ru",
                "BadJavaProgrammer",
                "Barney Gumble",
                LocalDate.of(1989, 4, 20));

        Map<Integer, User> users = new HashMap<>();
        users.put(1, testUser);
        users.put(2, testUser2);

        test.createUser(testUser, request);
        test.createUser(testUser2,request);

        Collection<User> result = test.getUsers();
        assertTrue(result.containsAll(users.values()));
    }
}