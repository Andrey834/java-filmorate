package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UsersServiceTest extends FilmorateApplicationTests {
    private User user1;
    private User user2;
    private User user3;
    private UsersService service;
    private Map<Integer, User> users;

    @BeforeEach
    public void setUp() {
        service = new UsersService();
        user1 = new User(
                0,
                "andy@mail.ru",
                "andy1",
                "Andry Chef",
                LocalDate.of(1990,1,1)
        );

        user2 = new User(
                0,
                "carl@yandex.ru",
                "carl2",
                "Carl Leaf",
                LocalDate.of(1980,1,1)
        );

        user3 = new User(
                0,
                "empty@yandex.ru",
                "Jorge",
                "",
                LocalDate.of(1970,1,1)
        );

        users = service.usersMap();
    }

    @AfterEach
    public void tearDown() {
        service.clearUsers();
    }

    @Test
    void addUser() {
        int expectedSizeMap = 0;
        int actualSizeMap = users.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Map is not empty before start");

        user1 = service.addUser(user1);

        expectedSizeMap = 1;
        actualSizeMap = users.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Collection size must be 1");

        user2 = service.addUser(user2);

        expectedSizeMap = 2;
        actualSizeMap = users.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Collection size must be 2");

        assertTrue(users.containsValue(user1), "user1 is missing from the collection");
        assertTrue(users.containsValue(user1), "user2 is missing from the collection");

        user3 = service.addUser(user3);

        String actualName = user3.getName();
        String expectedName = user3.getLogin();
        assertEquals(expectedName, actualName, "Name must be the same as Login");

    }

    @Test
    void updateUser() {
        int expectedSizeMap = 0;
        int actualSizeMap = users.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Map is not empty before start");
        service.addUser(user1);

        expectedSizeMap = 1;
        actualSizeMap = users.size();
        assertEquals(expectedSizeMap, actualSizeMap, "Collection size must be 1");

        User actualUser = new User(
                1,
                "empty@rambler.ru",
                "Jorge",
                "Carlito",
                LocalDate.of(1960,3,3)
        );

        User expectedUser = service.updateUser(actualUser);

        assertEquals(actualUser, expectedUser, "User hasn't been updated.");
    }

    @Test
    void getUsers() {
        List<User> usersList = service.getUsers();
        int expectedSizeList = 0;
        int actualSizeList = usersList.size();
        assertEquals(expectedSizeList, actualSizeList, "List is not empty before start");

        User expectedFilm1 = service.addUser(user1);
        User expectedFilm2 = service.addUser(user2);

        usersList = service.getUsers();
        expectedSizeList = 2;
        actualSizeList = usersList.size();
        assertEquals(expectedSizeList, actualSizeList, "List size should be 2");

        assertTrue(usersList.contains(expectedFilm1), "User1 not listed");
        assertTrue(usersList.contains(expectedFilm2), "User2 not listed");
    }
}