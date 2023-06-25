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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmsService;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmsControllerTest extends FilmorateApplicationTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private FilmsService service;
    private ObjectMapper objectMapper;
    private Film film1;
    private Film film2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        film1 = new Film(
                0,
                "film1",
                "desc1",
                LocalDate.of(2015, 12, 12),
                120
        );

        film2 = new Film(
                0,
                "film2",
                "desc2",
                LocalDate.of(2017, 12, 12),
                120
        );
    }

    @AfterEach
    void downTear() {
        service.clearFilms();
    }

    @Test
    void addFilm() throws Exception {
        MvcResult mvcResult = sendPost(film1).andExpect(status().is2xxSuccessful()).andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();


        Film expectedFilm = film1;
        Film actualFilm = objectMapper.readValue(responseBody, Film.class);

        assertEquals(expectedFilm, actualFilm, "Expected film1 is different");
    }

    @Test
    void whenNameIsEmpty() throws Exception {
        List<String> emptyNames = List.of(
                "  ",
                " ",
                ""
        );

        for (String name : emptyNames) {
            film1.setName(name);
            sendPost(film1).andExpect(status().is4xxClientError());
        }

        int expectedSizeList = 0;
        int actualSizeList = service.getFilms().size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be EMPTY");
    }

    @Test
    void whenDescriptionLength200Char() throws Exception {
        film1.setDescription("a".repeat(200));

        sendPost(film1).andExpect(status().is2xxSuccessful());

        int expectedSizeList = 1;
        int actualSizeList = service.getFilms().size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be 1");
    }

    @Test
    void whenDescriptionLength201Char() throws Exception {
        film1.setDescription("a".repeat(201));

        sendPost(film1).andExpect(status().is4xxClientError());

        int expectedSizeList = 0;
        int actualSizeList = service.getFilms().size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be EMPTY");
    }

    @Test
    void whenReleaseDateBeforeAndAfterValidDate() throws Exception {
        LocalDate minValidDate = LocalDate.of(1895, 12, 28);
        film1.setReleaseDate(minValidDate);

        sendPost(film1).andExpect(status().is2xxSuccessful());

        int expectedSizeList = 1;
        int actualSizeList = service.getFilms().size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be 1");

        LocalDate notValidDate = minValidDate.minusDays(1);
        film2.setReleaseDate(notValidDate);

        sendPost(film2).andExpect(status().is4xxClientError());

        actualSizeList = service.getFilms().size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be 1");

        LocalDate validDate = minValidDate.plusDays(1);
        film2.setReleaseDate(validDate);

        sendPost(film2).andExpect(status().is2xxSuccessful());

        List<Film> actualList = service.getFilms();
        film1.setId(1);
        film2.setId(2);

        assertTrue(actualList.contains(film1));
        assertTrue(actualList.contains(film2));
    }

    @Test
    void whenAddFilmWithNegativeValueShouldReturnStatus400() throws Exception {
        int negativeDuration = -1;
        film1.setDuration(negativeDuration);

        sendPost(film1).andExpect(status().is4xxClientError());

        int expectedSizeList = 0;
        int actualSizeList = service.getFilms().size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be Empty");
    }

    @Test
    void updateFilm() throws Exception {
        sendPost(film1).andExpect(status().is2xxSuccessful());
        sendPost(film2).andExpect(status().is2xxSuccessful());

        int expectedSizeList = 2;
        int actualSizeList = service.getFilms().size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be 2");

        Film updateFilm1 = film1;
        String expectedName = "updateFilm1";
        int expectedId = 1;
        updateFilm1.setId(expectedId);
        updateFilm1.setName(expectedName);

        sendPut(updateFilm1).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.name").value(expectedName));
    }

    @Test
    void updateFilmWithNotValidName() throws Exception {
        sendPost(film1).andExpect(status().is2xxSuccessful());
        sendPost(film2).andExpect(status().is2xxSuccessful());

        int expectedSizeList = 2;
        int actualSizeList = service.getFilms().size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be 2");

        Film updateFilmWithNotValidName = film1;
        String notValidName = "";
        updateFilmWithNotValidName.setName(notValidName);

        sendPut(updateFilmWithNotValidName).andExpect(status().is4xxClientError());

        List<Film> actualList = service.getFilms();

        assertFalse(actualList.contains(updateFilmWithNotValidName));
    }

    @Test
    void updateFilmWithNotValidDescription() throws Exception {
        sendPost(film1).andExpect(status().is2xxSuccessful());
        sendPost(film2).andExpect(status().is2xxSuccessful());

        int expectedSizeList = 2;
        int actualSizeList = service.getFilms().size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be 2");

        int maxLengthDescriptionPlus1 = 201;
        Film updateFilmWithNotValidDescription = film1;
        updateFilmWithNotValidDescription.setDescription("a".repeat(maxLengthDescriptionPlus1));

        sendPut(updateFilmWithNotValidDescription).andExpect(status().is4xxClientError());
    }

    @Test
    void updateFilmWithNotValidReleaseDate() throws Exception {
        sendPost(film1).andExpect(status().is2xxSuccessful());
        sendPost(film2).andExpect(status().is2xxSuccessful());

        int expectedSizeList = 2;
        int actualSizeList = service.getFilms().size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be 2");

        LocalDate beforeValidDateForFilm1 = LocalDate.of(1895, 12, 27);
        LocalDate futureDateForFilm1 = LocalDate.now().plusDays(1);

        Film updateFilmWithNotValidReleaseDate = film1;
        updateFilmWithNotValidReleaseDate.setReleaseDate(beforeValidDateForFilm1);

        sendPut(updateFilmWithNotValidReleaseDate).andExpect(status().is4xxClientError());

        LocalDate expectedDate = film1.getReleaseDate();
        LocalDate actualDate = updateFilmWithNotValidReleaseDate.getReleaseDate();
        assertEquals(expectedDate, actualDate);

        updateFilmWithNotValidReleaseDate.setReleaseDate(futureDateForFilm1);

        sendPut(updateFilmWithNotValidReleaseDate).andExpect(status().is4xxClientError());
    }

    @Test
    void getFilms() throws Exception {
        List<Film> actualList = service.getFilms();
        int expectedSizeList = 0;
        int actualSizeList = actualList.size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be Empty");

        sendPost(film1).andExpect(status().is2xxSuccessful());
        sendPost(film2).andExpect(status().is2xxSuccessful());

        actualList = service.getFilms();
        expectedSizeList = 2;
        actualSizeList = actualList.size();

        assertEquals(expectedSizeList, actualSizeList, "List size should be 2");

        assertTrue(actualList.contains(film1));
        assertTrue(actualList.contains(film2));

        String responseBody = sendGet().andReturn().getResponse().getContentAsString();
        List<Film> filmList = objectMapper.readValue(responseBody, new TypeReference<>(){});
        int actualSizeResponseList = filmList.size();

        assertTrue(filmList.contains(film1));
        assertTrue(filmList.contains(film2));

        assertEquals(expectedSizeList, actualSizeResponseList, "List size should be 2");
    }

    private ResultActions sendPost(Film film) throws Exception {
        String json = objectMapper.writeValueAsString(film);
        return mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions sendPut(Film film) throws Exception {
        String json = objectMapper.writeValueAsString(film);
        return mockMvc.perform(put("/films").content(json).contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions sendGet() throws Exception {
        return mockMvc.perform(get("/films").contentType(MediaType.APPLICATION_JSON));
    }
}