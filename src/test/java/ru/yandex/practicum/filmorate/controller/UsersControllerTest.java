package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UsersService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest extends FilmorateApplicationTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private UsersService service;
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {

        user1 = new User(0, "andrey@mail.ru", "andr", "porky", LocalDate.of(2000, 1, 1));

        user2 = new User(0, "max@rambler.ru", "maxr", "bubby", LocalDate.of(1990, 1, 1));
    }

    @AfterEach
    void downTear() {
        service.clearUsers();
    }

    @Test
    void addUser() throws Exception {
        String responseBody = sendPost(user1)
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User expectedUser = user1;
        User actualUser = objectMapper.readValue(responseBody, User.class);

        assertEquals(expectedUser, actualUser, "Expected user1 is different");

        int expectedId = 1;
        int actualId = actualUser.getId();

        assertEquals(expectedId, actualId, "Expected user1 should be ID -" + expectedId);
    }

    @Test
    void whenAddUserWithNotValidEmailThenUserShouldNotBeAdded() throws Exception {
        List<String> incorrectlyEmails = List.of(
          "@example.com",
          "example@example.com@",
          "example@.com",
          "example example.ru",
          "example!example.ru",
          "example.ru@",
          "@",
          " ",
          ""
        );

        for (String incorrectlyEmail : incorrectlyEmails) {
            user1.setEmail(incorrectlyEmail);
            sendPost(user1).andExpect(status().is4xxClientError());
        }
    }

    @Test
    void whenAddUserWithNotValidLoginThenUserShouldNotBeAdded() throws Exception {
        List<String> incorrectlyLogins = List.of(
                " ",
                "",
                "example root",
                "example ",
                " example",
                " example "
        );

        for (String incorrectlyLogin : incorrectlyLogins) {
            user1.setLogin(incorrectlyLogin);
            sendPost(user1).andExpect(status().is4xxClientError());
        }
    }

    @Test
    void whenAddUserWithEmptyNameThenNameShouldBeSameLogin() throws Exception {
        List<String> emptyName = List.of(
                "",
                " "
        );

        for (String name : emptyName) {
            user1.setName(name);

            String responseBody = sendPost(user1)
                    .andExpect(status().is2xxSuccessful())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String expectedName = user1.getLogin();
            String actualName = objectMapper.readValue(responseBody, User.class).getName();

            assertEquals(expectedName, actualName, "Name should be " + expectedName);
        }
    }

    @Test
    void whenAddUserWithFutureBirthdayThenThenUserShouldNotBeAdded() throws Exception {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        user1.setBirthday(futureDate);

        sendPost(user1).andExpect(status().is4xxClientError());

        int expectedSizeUserList = 0;
        int actualSizeUserList = service.getUsers().size();

        assertEquals(expectedSizeUserList, actualSizeUserList, "List size should be " + expectedSizeUserList);
    }

    @Test
    void updateFilm() throws Exception {
        sendPost(user1).andExpect(status().is2xxSuccessful());
        sendPost(user2).andExpect(status().is2xxSuccessful());

        String expectedNewEmail = "new@mail.ru";
        String expectedName = "newName";
        int userIdForUpdate = 1;
        user1.setId(userIdForUpdate);
        user1.setEmail(expectedNewEmail);
        user1.setName(expectedName);

        String responseBody = sendPut(user1)
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String actualEmail = objectMapper.readValue(responseBody, User.class).getEmail();
        String actualName = objectMapper.readValue(responseBody, User.class).getName();

        assertEquals(expectedNewEmail, actualEmail, "Email should be " + expectedNewEmail);
        assertEquals(expectedName, actualName, "Name should be " + expectedName);
    }

    @Test
    void whenUpdateUserWithNotValidEmailThenUserShouldNotBeUpdate() throws Exception {
        String responseBody = sendPost(user1)
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int idUpdatedUser = objectMapper.readValue(responseBody, User.class).getId();
        user1.setId(idUpdatedUser);

        List<String> incorrectlyEmails = List.of(
                "@example.com",
                "example@example.com@",
                "example@.com",
                "example example.ru",
                "example!example.ru",
                "example.ru@",
                "@",
                " ",
                ""
        );

        for (String incorrectlyEmail : incorrectlyEmails) {
            user1.setEmail(incorrectlyEmail);
            sendPost(user1).andExpect(status().is4xxClientError());
        }
    }

    @Test
    void whenUpdateUserWithEmptyLoginThenUserShouldNotBeUpdate() throws Exception {
        String responseBody = sendPost(user1)
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int idUpdatedUser = objectMapper.readValue(responseBody, User.class).getId();
        user1.setId(idUpdatedUser);

        List<String> emptyLogins = List.of(
                "  ",
                " ",
                ""
        );

        for (String emptyLogin : emptyLogins) {
            user1.setLogin(emptyLogin);
            assertTrue(user1.getLogin().isBlank());
            sendPut(user1).andExpect(status().is4xxClientError());
        }
    }

    @Test
    void whenUpdateUserWithEmptyNameThenNameShouldBeSameLogin() throws Exception {
        String responseBody = sendPost(user1)
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int idUpdatedUser = objectMapper.readValue(responseBody, User.class).getId();
        user1.setId(idUpdatedUser);

        List<String> emptyNames = List.of(
                "  ",
                " ",
                ""
        );

        for (String name : emptyNames) {
            user1.setName(name);
            assertTrue(user1.getName().isBlank(), "Name should be empty");

            String user = sendPut(user1).andExpect(status().is2xxSuccessful())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            User actualUser = objectMapper.readValue(user, User.class);

            String expectedName = actualUser.getLogin();
            String actualName = actualUser.getName();
            System.out.println(actualUser);

            assertEquals(expectedName, actualName, "Name should be " + expectedName);
        }

    }

    @Test
    void whenUpdateUserWithFutureDateThenUserShouldNotBeAdded() throws Exception {
        String responseBody = sendPost(user1)
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User user = objectMapper.readValue(responseBody, User.class);

        LocalDate futureDate = LocalDate.now().plusDays(1);
        int idUpdatedUser = user.getId();

        user1.setId(idUpdatedUser);
        user1.setBirthday(futureDate);

        sendPut(user1).andExpect(status().is4xxClientError());

        LocalDate actualDate = service.getUsers().get(0).getBirthday();

        assertFalse(futureDate.isEqual(actualDate));
    }



    @Test
    void getFilms() throws Exception {
        List<User> actualList = service.getUsers();
        int expectedSizeList = 0;
        int actualSizeList = actualList.size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be Empty");

        sendPost(user1).andExpect(status().is2xxSuccessful());
        sendPost(user2).andExpect(status().is2xxSuccessful());

        actualList = service.getUsers();
        expectedSizeList = 2;
        actualSizeList = actualList.size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be 2");

        String responseBody = sendGet().andReturn().getResponse().getContentAsString();
        List<User> filmList = objectMapper.readValue(responseBody, new TypeReference<>(){});
        int actualSizeResponseList = filmList.size();

        assertTrue(filmList.contains(user1));
        assertTrue(filmList.contains(user2));

        assertEquals(expectedSizeList, actualSizeResponseList, "List size should be 2");
    }

    private ResultActions sendPost(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);
        return mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions sendPut(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);
        return mockMvc.perform(put("/users").content(json).contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions sendGet() throws Exception {
        return mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON));
    }
}