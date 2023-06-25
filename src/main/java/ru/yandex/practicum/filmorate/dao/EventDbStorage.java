package ru.yandex.practicum.filmorate.dao;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.EventStorage;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperations;
import ru.yandex.practicum.filmorate.model.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class EventDbStorage implements EventStorage {
	private static final String GET_EVENTS = "SELECT * FROM events WHERE user_id = ?";
	private static final String GET_EVENT_BY_ID = "SELECT * FROM events WHERE id = ?";
	private final JdbcTemplate jdbcTemplate;

	public EventDbStorage(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Event> getEvents(Long userId) {
		log.info("Start EventDbStorage.getEvents userId:{}.", userId);
		List<Event> events = jdbcTemplate.query(GET_EVENTS, this::mapRowToEvent, userId);
		log.info("End EventDbStorage.getEvents userId:{}.List<Event> events.size = {}", userId, events.size());
		return events;
	}

	@Override
	public void addEvent(Event event) {
		log.info("Start EventDbStorage.addEvent event:{}.", event);
		SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("events")
				.usingGeneratedKeyColumns("id");
		long eventId = insert.executeAndReturnKey(event.toMapEvent()).intValue();
		event.setEventId(eventId);
		log.info("End EventDbStorage.addEvent event was added:{}.", event);
	}

	private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
		return new Event(
				rs.getLong("id"),
				rs.getLong("date"),
				rs.getLong("user_id"),
				EventType.valueOf(rs.getString("event_type")),
				EventOperations.valueOf(rs.getString("operation")),
				rs.getLong("entity_id")
		);
	}
}
