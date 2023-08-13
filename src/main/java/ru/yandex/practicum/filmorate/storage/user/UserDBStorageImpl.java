package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.Objects;
import java.util.List;
import java.sql.Date;

@Component("userDbStorage")
@EnableAutoConfiguration
@RequiredArgsConstructor
@Slf4j
public class UserDBStorageImpl implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM USERS";

        return jdbcTemplate.query(sql, (rs, rowNum) -> userBuilder(rs));
    }

    @Override
    public Optional<User> getUserById(Integer userId) {

        String sql = "SELECT * FROM USERS WHERE ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, userId);
        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getInt("id"))
                    .name(userRows.getString("name"))
                    .login(Objects.requireNonNull(userRows.getString("login")))
                    .email(Objects.requireNonNull(userRows.getString("email")))
                    .birthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate())
                    .build();

            user.getFriends().addAll(Objects.requireNonNull(getFriends(userId)));
            log.info("Найден пользователь с ID={}", userId);
            return Optional.of(user);
        } else {
            log.info("Пользователь с ID={} не найден!", userId);
            return Optional.empty();
        }
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO USERS (ID, EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(
                sql,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
        );
        log.info("Добавлен новый пользователь с ID={}", user.getId());
        return user;
    }

    @Override
    public Optional<User> updateUser(User user) {
        String sql1 = "DELETE FROM USERS WHERE ID = ?";
        String sql2 = "INSERT INTO USERS (ID, EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql1, user.getId());
        jdbcTemplate.update(
                sql2,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        log.info("Пользователь с ID={} обновлен", user.getId());
        return getUserById(user.getId());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User friend = getUserById(friendId).get();

        boolean friendStatus = false;
        if (friend.getFriends().contains(userId)) {
            friendStatus = true;
            String sql = "UPDATE USER_FRIENDS SET USER_ID = ? AND FRIENDS_ID = ? AND STATUS = ? " +
                    "WHERE USER_ID = ? AND FRIENDS_ID = ?";
            jdbcTemplate.update(
                    sql,
                    userId,
                    friendId,
                    true,
                    userId,
                    friendId
            );
            log.info("Пользователь ID={} подружился с пользователем ID={}", userId, friendId);
            log.info("Дружба между пользователями ID={} и ID={} стала взаимной", userId, friendId);
        }
        String sql = "INSERT INTO USER_FRIENDS (USER_ID, FRIENDS_ID, STATUS) VALUES (?, ?, ?)";
        jdbcTemplate.update(
                sql,
                userId,
                friendId,
                friendStatus
        );
        log.info("Пользователь ID={} подружился с пользователем ID={}", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        User friend = getUserById(friendId).get();

        String sql = "DELETE FROM USER_FRIENDS WHERE USER_ID = ? AND FRIENDS_ID = ?";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Пользователь ID={} удалил из друзей пользователя ID={}", userId, friendId);
        if (friend.getFriends().contains(userId)) {
            sql = "UPDATE USER_FRIENDS SET USER_ID = ? AND FRIENDS_ID = ? AND STATUS = ? " +
                    "WHERE USER_ID = ? AND FRIENDS_ID = ?";
            jdbcTemplate.update(
                    sql,
                    friendId,
                    userId,
                    false,
                    friendId,
                    userId
            );
            log.info("Дружба между пользователями ID={} и ID={} стала невзаимной", userId, friendId);
        }
    }

    private List<Integer> getFriends(int userId) {
        String sql = "SELECT FRIENDS_ID FROM USER_FRIENDS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friends_id"), userId);
    }

    private User userBuilder(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        User user = User.builder()
                .id(id)
                .name(rs.getString("name"))
                .login(Objects.requireNonNull(rs.getString("login")))
                .email(Objects.requireNonNull(rs.getString("email")))
                .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                .build();

        user.getFriends().addAll(getFriends(id));
        return user;
    }
}
