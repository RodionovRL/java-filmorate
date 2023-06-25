package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Event {
	private Long eventId;
	private Long userId;
	private Long entityId;
	private EventType eventType;
	private EventOperations operation;
	private Long timestamp;

	public Event(Long eventId, Long timestamp, Long userId, EventType eventType, EventOperations operation, Long entityId) {
		this.eventId = eventId;
		this.userId = userId;
		this.entityId = entityId;
		this.eventType = eventType;
		this.operation = operation;
		this.timestamp = timestamp;
	}

	public Event(Long entityId, Long userId, EventType eventType, EventOperations operation) {
		this.entityId = entityId;
		this.userId = userId;
		this.eventType = eventType;
		this.operation = operation;
		this.timestamp = Instant.now().toEpochMilli();
	}

	public Map<String, Object> toMapEvent() {
		Map<String, Object> values = new HashMap<>();
		values.put("id", eventId);
		values.put("time_stamp", timestamp);
		values.put("user_id", userId);
		values.put ("event_type", eventType);
		values.put("event_operation", operation);
		values.put("entity_id", entityId);
		return values;
	}
}