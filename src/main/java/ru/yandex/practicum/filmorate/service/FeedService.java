package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.EventStorage;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final EventStorage eventStorage;

    public Event userAddLike(long userId, long filmId) {
        return addEvent(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    public Event userRemoveLike(long userId, long filmId) {
        return addEvent(userId, filmId, EventType.LIKE, Operation.REMOVE);
    }

    public Event userUpdateLike(long userId, long filmId) {
        return addEvent(userId, filmId, EventType.LIKE, Operation.UPDATE);
    }

    public Event userAddReview(long userId, long reviewId) {
        return addEvent(userId, reviewId, EventType.REVIEW, Operation.ADD);
    }

    public Event userRemoveReview(long userId, long reviewId) {
        return addEvent(userId, reviewId, EventType.REVIEW, Operation.REMOVE);
    }

    public Event userUpdateReview(long userId, long reviewId) {
        return addEvent(userId, reviewId, EventType.REVIEW, Operation.UPDATE);
    }

    public Event useAddFriend(long userId, long friendId) {
        return addEvent(userId, friendId, EventType.FRIEND, Operation.ADD);
    }

    public Event useRemoveFriend(long userId, long friendId) {
        return addEvent(userId, friendId, EventType.FRIEND, Operation.REMOVE);
    }

    public Event useUpdateFriend(long userId, long friendId) {
        return addEvent(userId, friendId, EventType.FRIEND, Operation.UPDATE);
    }

    public List<Event> getFeedById(long userId) {
        return eventStorage.getLastEvents(userId);
    }

    private Event addEvent(long userId, long entityId, EventType eventType, Operation operation) {
        return eventStorage.addEvent(Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .date(Instant.now())
                .build());
    }
}
