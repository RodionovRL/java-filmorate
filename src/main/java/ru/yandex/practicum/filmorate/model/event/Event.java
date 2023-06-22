package ru.yandex.practicum.filmorate.model.event;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Value
public class Event {
    long eventID;
    @NotBlank(message = "ID пользователя не может быть пустым")
    long userId;
    @NotBlank(message = "ID сущности не может быть пустым")
    long entityId;
    EventType eventType;
    Operation operation;
    Instant timestamp = Instant.now();
    
}
