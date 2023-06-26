package ru.yandex.practicum.filmorate.model.event;

import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.Map;

@Value
@Builder
public class Event {
    @NonFinal
    @Setter
    long eventId;
    @NotBlank(message = "ID пользователя не может быть пустым")
    long userId;
    @NotBlank(message = "ID сущности не может быть пустым")
    long entityId;
    EventType eventType;
    Operation operation;
    @NonFinal
    long timestamp;

    public Map<String, Object> toMap() {
        return Map.of("creation_date", new Timestamp(timestamp),
                "user_id", userId,
                "event_type", eventType.ordinal() + 1,
                "operation", operation.ordinal() + 1,
                "entity_id", entityId);
    }
}
