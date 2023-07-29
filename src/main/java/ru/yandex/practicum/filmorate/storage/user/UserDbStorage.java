package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User save(User user) {
        String sqlQuery = "INSERT INTO USERS(email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return findUserById(id);
    }

    @Override
    public User findUserById(Long id) {
        String sqlQuery = "select * from USERS where id = ?";
        String queryFriend = "select ID_TO from USERS as U right join FRIENDS as F on U.ID = F.ID_FROM where U.ID = ?";
        String queryRequest = "select ID_FROM from USERS as U right join FRIENDS as F on U.ID = F.ID_TO where U.ID = ?";

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        List<Long> friends = jdbcTemplate.queryForList(queryFriend, Long.class, id);
        List<Long> requestFriends = jdbcTemplate.queryForList(queryRequest, Long.class, id);

        if (userRows.next()) {
            User user = new User(userRows.getLong("id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate(),
                    new HashSet<>(friends),
                    new HashSet<>(requestFriends));
            for (Long aLong : friends) {
                user.checkFriends(aLong);
            }

            return user;
        } else return null;
    }

    @Override
    public List<User> findUsers() {
        String sqlQuery = "select * from USERS";

        List<User> users = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                Objects.requireNonNull(rs.getDate("birthday")).toLocalDate(),
                new HashSet<>(getFriends(rs.getLong("id"))),
                new HashSet<>()));

        users.sort(Comparator.comparingLong(User::getId));

        return users;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE ID = ?";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            ps.setLong(5, user.getId());

            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return findUserById(id);
    }

    @Override
    public boolean addFriend(Long idUser, Long idFriend) {
        String sqlQuery = "INSERT INTO FRIENDS(ID_FROM, ID_TO) VALUES (?, ?)";

        return jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setLong(1, idUser);
            ps.setLong(2, idFriend);
            return ps;
        }) > 0;
    }

    @Override
    public boolean removeFriend(Long idUser, Long idFriend) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE ID_FROM = ? AND ID_TO = ?";

        return jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setLong(1, idUser);
            ps.setLong(2, idFriend);
            return ps;
        }) > 0;
    }

    private List<Long> getFriends(Long userId) {
        String sqlQuery = "SELECT ID_TO FROM FRIENDS WHERE ID_FROM = ?";

        List<Long> friends = jdbcTemplate.queryForList(sqlQuery, Long.class, userId);

        Collections.sort(friends);

        return friends;
    }
}
