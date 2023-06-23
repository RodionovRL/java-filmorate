package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.EventStorage;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Event addEvent(Event event) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("id");
        event.setEventId(
                insert.executeAndReturnKey(event.toMap()).longValue()
        );
        return event;
    }

    @Override
    public List<Event> getLastEvents(long userId) {
        String sqlQuery = "SELECT e.ID AS eventID,\n" +
                "       e.USER_ID AS userId,\n" +
                "       e.ENTITY_ID AS entityId,\n" +
                "       e.\"DATE\"  AS \"date\",\n" +
                "       ot.NAME AS operation,\n" +
                "       et.NAME AS eventType\n" +
                "FROM EVENTS AS e \n" +
                "LEFT JOIN OPERATION_TYPE AS ot ON e.OPERATION=ot.ID\n" +
                "LEFT JOIN EVENT_TYPE AS et ON e.EVENT_TYPE=et.ID\n" +
                "WHERE e.ID=?\n" +
                "ORDER BY e.\"DATE\" DESC ";

        return jdbcTemplate.query(sqlQuery, this::makeEvent);
    }

    private Event makeEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("eventID"))
                .userId(rs.getLong("userId"))
                .entityId(rs.getLong("entityId"))
                .date(rs.getDate("date").toInstant())
                .operation(Operation.valueOf(rs.getString("operation")))
                .eventType(EventType.valueOf(rs.getString("eventType")))
                .build();
    }
}
