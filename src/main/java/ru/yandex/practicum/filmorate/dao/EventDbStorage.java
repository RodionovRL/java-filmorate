package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.EventStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        if (!isUserExist(userId)) {
            throw new UserNotFoundException(
                    String.format("Попытка получить фид несуществующего пользователя с id=%d", userId)
            );
        }
        String sqlQuery = " SELECT e.ID AS eventID, " +
                "       e.USER_ID AS userId, " +
                "       e.ENTITY_ID AS entityId, " +
                "       e.CREATION_DATE AS d, " +
                "       ot.NAME AS operation, " +
                "       et.NAME AS eventType " +
                " FROM EVENTS AS e " +
                " LEFT JOIN OPERATION_TYPE AS ot ON e.OPERATION=ot.ID " +
                " LEFT JOIN EVENT_TYPE AS et ON e.EVENT_TYPE=et.ID " +
                " WHERE e.USER_ID=? " +
                " ORDER BY e.CREATION_DATE ASC ";
        return jdbcTemplate.query(sqlQuery, this::makeEvent, userId);
    }

    private boolean isUserExist(Long userId) {
        if (userId < 1) {
            return false;
        }
        String sqlQuery = "SELECT " +
                "EXISTS (SELECT id" +
                "        FROM USERS " +
                "        WHERE id=?)";
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(sqlQuery,
                        (rs, rowNum) -> rs.getBoolean(rs.getMetaData().getColumnName(1)),
                        userId)
        );
    }

    private Event makeEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("eventID"))
                .userId(rs.getLong("userId"))
                .entityId(rs.getLong("entityId"))
                .timestamp(rs.getTimestamp("d").toInstant().toEpochMilli())
                .operation(Operation.valueOf(rs.getString("operation")))
                .eventType(EventType.valueOf(rs.getString("eventType")))
                .build();
    }

    @Override
    public int userIdByReviewId(Long reviewId) {
        String sqlQuery = "SELECT USER_ID  " +
                "FROM REVIEW r  " +
                "WHERE ID=?" +
                "LIMIT 1";
        Integer userId = jdbcTemplate.queryForObject(sqlQuery, Integer.class, reviewId);
        if (userId == null) {
            throw new UserNotFoundException(
                    String.format("Попытка получить пользователя не остовлявшего ревью id %d", reviewId)
            );
        }
        return userId;
    }
}
