package ru.yandex.practicum.filmorate.api;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

	List<Event> getEvents(Long id);

	void addEvent(Event event);
}
