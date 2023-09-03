package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Component("eventStorage")
@RequiredArgsConstructor
public class EventStorageImpl implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void writeEvent(int userId, EventType eventType, Operation operation, int entityId) {
        String sql = "INSERT INTO EVENTS (CREATE_TIME, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) VALUES (?,?,?,?,?)";

        jdbcTemplate.update(sql,
                Instant.now(),
                userId,
                eventType.getValue(),
                operation.getValue(),
                entityId
        );
    }

    @Override
    public List<Event> getEvents(int userId) {
        String sql = "SELECT CREATE_TIME, USER_ID, EVENT_TYPE, OPERATION, EVENT_ID, ENTITY_ID " +
                     "FROM EVENTS " +
                     "WHERE USER_ID = ? " +
                     "ORDER BY CREATE_TIME";

        return jdbcTemplate.query(sql, (rs, rowNum) -> eventBuilder(rs), userId);
    }

    private Event eventBuilder(ResultSet rs) throws SQLException {
        return Event.builder()
                .eventId(rs.getInt("EVENT_ID"))
                .createTime(rs.getTimestamp("CREATE_TIME").toInstant())
                .eventType(EventType.valueOf(rs.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(rs.getString("OPERATION")))
                .userId(rs.getInt("USER_ID"))
                .entityId(rs.getInt("ENTITY_ID"))
                .build();
    }
}
