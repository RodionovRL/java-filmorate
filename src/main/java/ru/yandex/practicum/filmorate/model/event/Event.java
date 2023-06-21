package ru.yandex.practicum.filmorate.model.event;

import lombok.Value;

import java.time.Instant;

@Value
public class Event {
    long eventID;
    long userId;
    long entityId;
    EventType eventType;
    Operation operation;
    Instant timestamp = Instant.now();

}
