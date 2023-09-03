package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Objects;
import java.util.List;
import java.sql.Date;
import java.util.Set;

@Component("userStorage")
@EnableAutoConfiguration
@RequiredArgsConstructor
@Slf4j
public class UserStorageImpl implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> userBuilder(rs));
    }

    @Override
    public List<User> getSelectedUsers(Integer... usersNum) {
        String inSql = String.join(",", Collections.nCopies(usersNum.length, "?"));
        return jdbcTemplate.query(
                String.format("SELECT * FROM USERS WHERE ID IN (%S)", inSql),
                (rs, rowNum) -> userBuilder(rs),
                (Object[]) usersNum
        );
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM USERS WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> userBuilder(rs), userId);
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"ID"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        int userId = Objects.requireNonNull(keyHolder.getKey()).intValue();

        log.info("Добавлен новый пользователь с ID={}", userId);
        return getUserById(userId);
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE ID = ?";

        jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        log.info("Пользователь с ID={} обновлен", user.getId());
        return getUserById(user.getId());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        Set<Integer> friends = getUserById(friendId).getFriends();

        boolean friendStatus = false;
        if (friends.contains(userId)) {
            friendStatus = true;
            String sql = "UPDATE USER_FRIENDS " +
                         "SET USER_ID = ? AND FRIENDS_ID = ? AND STATUS = ? " +
                         "WHERE USER_ID = ? AND FRIENDS_ID = ?";
            jdbcTemplate.update(
                    sql,
                    userId,
                    friendId,
                    friendStatus,
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
        User friend = getUserById(friendId);

        String sql = "DELETE FROM USER_FRIENDS " +
                     "WHERE USER_ID = ? AND FRIENDS_ID = ?";

        jdbcTemplate.update(sql, userId, friendId);
        log.info("Пользователь ID={} удалил из друзей пользователя ID={}", userId, friendId);

        if (friend.getFriends().contains(userId)) {
            sql = "UPDATE USER_FRIENDS " +
                  "SET USER_ID = ?   AND FRIENDS_ID = ? AND STATUS = ? " +
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

    @Override
    public void deleteUser(int id) {
        jdbcTemplate.update("DELETE FROM USERS WHERE ID = ?", id);
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = "SELECT FRIENDS_ID AS id, NAME, LOGIN, EMAIL, BIRTHDAY " +
                     "FROM USER_FRIENDS AS uf " +
                     "JOIN users        AS u ON uf.FRIENDS_ID = u.id " +
                     "WHERE uf.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> userBuilder(rs), userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        String sql = "SELECT FRIENDS_ID AS id, NAME, LOGIN, EMAIL, BIRTHDAY " +
                     "FROM USER_FRIENDS AS uf " +
                     "JOIN users AS u ON uf.FRIENDS_ID = u.id " +
                     "WHERE USER_ID = ? AND u.id IN ( " +
                     "SELECT FRIENDS_ID " +
                     "FROM USER_FRIENDS " +
                     "WHERE USER_ID = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> userBuilder(rs), userId, friendId);
    }

    private List<Integer> getFriendListIds(int userId) {
        String sql = "SELECT FRIENDS_ID " +
                     "FROM USER_FRIENDS " +
                     "WHERE USER_ID = ?";
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

        user.getFriends().addAll(getFriendListIds(id));
        return user;
    }
}
