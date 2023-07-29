package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Mpa;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmorateApplicationTests {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private User user1;
    private User user2;
    private Film film1;
    private Film film2;

    @BeforeEach
    void setUp() {
        user1 = new User(
                0L,
                "example@mail.com",
                "example",
                "Joan Li",
                LocalDate.of(2000, 1, 1),
                new HashSet<>(),
                new HashSet<>()
        );

        user2 = new User(
                0L,
                "example2@mail.com",
                "example2",
                "Joan Li2",
                LocalDate.of(1990, 10, 10),
                new HashSet<>(),
                new HashSet<>()
        );

        film1 = new Film(
                0L,
                "film1",
                "description film1",
                LocalDate.of(2000, 1, 1),
                150,
                new Mpa(1L),
                new HashSet<>(),
                new HashSet<>()
        );

        film2 = new Film(
                0L,
                "film2",
                "description film2",
                LocalDate.of(2010, 2, 2),
                110,
                new Mpa(2L),
                new HashSet<>(),
                new HashSet<>()
        );
    }

    @Test
    public void testSaveUser() {
        int sizeDbUsers = userDbStorage.findUsers().size();

        assertThat(sizeDbUsers).isEqualTo(0);

        Optional<User> newUser = Optional.ofNullable(userDbStorage.save(user1));

        assertThat(newUser)
                .isPresent()
                .hasValueSatisfying(usr ->
                        assertThat(usr)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("email", user1.getEmail())
                                .hasFieldOrPropertyWithValue("login", user1.getLogin())
                                .hasFieldOrPropertyWithValue("name", user1.getName())
                                .hasFieldOrPropertyWithValue("birthday", user1.getBirthday())
                );
    }

    @Test
    public void testFindUserById() {
        userDbStorage.save(user1);

        Long id = 1L;
        Optional<User> userOptional = Optional.ofNullable(userDbStorage.findUserById(id));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testFindUsers() {
        int actualSizeUserList = userDbStorage.findUsers().size();
        int expectedSizeUserList = 0;

        assertThat(actualSizeUserList).isEqualTo(expectedSizeUserList);

        userDbStorage.save(user1);
        userDbStorage.save(user2);

        List<User> usersList = userDbStorage.findUsers();

        actualSizeUserList = usersList.size();
        expectedSizeUserList = 2;

        assertThat(actualSizeUserList).isEqualTo(expectedSizeUserList);

        assertThat(usersList).contains(user1, user2);
    }

    @Test
    public void testUpdateUser() {
        User oldUser = userDbStorage.save(user1);
        Long numberUserForUpdate = oldUser.getId();

        User updateUser = User.builder()
                .id(numberUserForUpdate)
                .name("Update")
                .email("update@mail.ru")
                .login("updateLogin")
                .birthday(LocalDate.of(1999, 12, 12))
                .friends(new HashSet<>())
                .requestAddFriends(new HashSet<>())
                .build();

        assertThat(oldUser).isNotEqualTo(updateUser);

        User expectedUser = userDbStorage.update(updateUser);

        assertThat(expectedUser).isEqualTo(updateUser);
    }

    @Test
    public void testAddFriend() {
        User friend1 = userDbStorage.save(user1);
        User friend2 = userDbStorage.save(user2);

        assertThat(friend1.getFriends().size()).isEqualTo(0);
        assertThat(friend2.getFriends().size()).isEqualTo(0);

        userDbStorage.addFriend(friend1.getId(), friend2.getId());

        friend1 = userDbStorage.findUserById(1L);
        friend2 = userDbStorage.findUserById(2L);

        assertThat(friend1.getFriends().size()).isEqualTo(1);
        assertThat(friend2.getRequestAddFriends().size()).isEqualTo(1);
    }

    @Test
    public void testRemoveFriend() {
        User friend1 = userDbStorage.save(user1);
        User friend2 = userDbStorage.save(user2);

        userDbStorage.addFriend(friend1.getId(), friend2.getId());

        friend1 = userDbStorage.findUserById(1L);
        friend2 = userDbStorage.findUserById(2L);

        assertThat(friend1.getFriends().size()).isEqualTo(1);
        assertThat(friend2.getRequestAddFriends().size()).isEqualTo(1);

        userDbStorage.removeFriend(friend1.getId(), friend2.getId());

        friend1 = userDbStorage.findUserById(1L);
        friend2 = userDbStorage.findUserById(2L);

        assertThat(friend1.getFriends().size()).isEqualTo(0);
        assertThat(friend2.getRequestAddFriends().size()).isEqualTo(0);
    }

    @Test
    public void testGetFriends() {
        User friend1 = userDbStorage.save(user1);
        User friend2 = userDbStorage.save(user2);

        Set<Long> friendList = friend1.getFriends();

        assertThat(friendList.size()).isEqualTo(0);

        userDbStorage.addFriend(friend1.getId(), friend2.getId());

        friend1 = userDbStorage.findUserById(friend1.getId());

        friendList = friend1.getFriends();

        assertThat(friendList).contains(friend2.getId());
        assertThat(friendList.size()).isEqualTo(1);
    }

    @Test
    public void testAddFilm() {
        int sizeDbFilms = filmDbStorage.getFilms().size();

        assertThat(sizeDbFilms).isEqualTo(0);

        Optional<Film> newFilm = Optional.ofNullable(filmDbStorage.addFilm(film1));

        assertThat(newFilm)
                .isPresent()
                .hasValueSatisfying(flm ->
                        assertThat(flm)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", film1.getName())
                                .hasFieldOrPropertyWithValue("description", film1.getDescription())
                                .hasFieldOrPropertyWithValue("releaseDate", film1.getReleaseDate())
                                .hasFieldOrPropertyWithValue("duration", film1.getDuration())
                );
    }

    @Test
    public void testFindFilmById() {
        filmDbStorage.addFilm(film1);

        Long id = 1L;
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.findFilmById(id));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testGetFilms() {
        Set<Film> films = filmDbStorage.getFilms();

        int actualSizeFilms = films.size();
        int expectedSizeFilms = 0;

        assertThat(actualSizeFilms).isEqualTo(expectedSizeFilms);

        Film actualFilm1 = filmDbStorage.addFilm(film1);
        Film actualFilm2 = filmDbStorage.addFilm(film2);

        films = filmDbStorage.getFilms();

        actualSizeFilms = films.size();
        expectedSizeFilms = 2;

        assertThat(actualSizeFilms).isEqualTo(expectedSizeFilms);

        assertThat(films).contains(actualFilm1, actualFilm2);
    }

    @Test
    public void testUpdateFilm() {
        Film oldFilm = filmDbStorage.addFilm(film1);
        Long numberFilmForUpdate = oldFilm.getId();

        Film expectedFilm = Film.builder()
                .id(numberFilmForUpdate)
                .name("Update FILM")
                .description("Update FILM description")
                .releaseDate(LocalDate.of(1999, 12, 12))
                .duration(200)
                .mpa(new Mpa(3L))
                .likes(new HashSet<>())
                .genres(new HashSet<>())
                .build();

        assertThat(oldFilm).isNotEqualTo(expectedFilm);

        Film actualFilm = filmDbStorage.updateFilm(expectedFilm);

        assertThat(actualFilm).isEqualTo(expectedFilm);
    }

    @Test
    public void testLike() {
        User actualUser1 = userDbStorage.save(user1);
        Film actualFilm1 = filmDbStorage.addFilm(film1);

        Long idActualFilm1 = actualFilm1.getId();
        Long idActualUser1 = actualUser1.getId();

        filmDbStorage.like(idActualFilm1, idActualUser1);

        Set<Long> likesFilm1 = filmDbStorage.findFilmById(idActualFilm1).getLikes();

        assertThat(likesFilm1).contains(idActualUser1).hasSize(1);
    }

    @Test
    public void testRemoveLike() {
        User actualUser1 = userDbStorage.save(user1);
        Film actualFilm1 = filmDbStorage.addFilm(film1);

        Long idActualFilm1 = actualFilm1.getId();
        Long idActualUser1 = actualUser1.getId();

        filmDbStorage.like(idActualFilm1, idActualUser1);

        Set<Long> likesFilm1 = filmDbStorage.findFilmById(idActualFilm1).getLikes();

        assertThat(likesFilm1).contains(idActualUser1).hasSize(1);

        filmDbStorage.removeLike(idActualFilm1, idActualUser1);

        likesFilm1 = filmDbStorage.findFilmById(idActualFilm1).getLikes();

        assertThat(likesFilm1).doesNotContain(idActualUser1).hasSize(0);
    }
}