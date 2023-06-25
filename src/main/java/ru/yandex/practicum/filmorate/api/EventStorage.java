package ru.yandex.practicum.filmorate.api;

import ru.yandex.practicum.filmorate.model.event.Event;

import java.util.List;

public interface EventStorage {
    Event addEvent(Event event);

    List<Event> getLastEvents(long userId);

    int userIdByReviewId(Long reviewId);
}
