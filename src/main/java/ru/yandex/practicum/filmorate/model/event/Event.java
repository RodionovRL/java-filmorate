package ru.yandex.practicum.filmorate.model.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
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
    Instant date;

    public Map<String, Object> toMap() {
        return Map.of("date", date,
                "user_id", userId,
                "event_type", eventType.ordinal() + 1,
                "operation", operation.ordinal() + 1,
                "entity_id", entityId);
    }

}
