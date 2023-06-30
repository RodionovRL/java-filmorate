package ru.yandex.practicum.filmorate.model.event;

import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Value
@Builder
public class Event {
    @NotBlank(message = "ID пользователя не может быть пустым")
    long userId;
    @NotBlank(message = "ID сущности не может быть пустым")
    long entityId;
    EventType eventType;
    Operation operation;
    @NonFinal
    @Setter
    long eventId;
    @NonFinal
    long timestamp;

    public Map<String, Object> toMap() {
        return Map.of("creation_date", new Timestamp(timestamp),
                "user_id", userId,
                "event_type", eventType.ordinal() + 1,
                "operation", operation.ordinal() + 1,
                "entity_id", entityId);
    }

    public static Event userAddLike(long userId, long filmId) {
        return addEvent(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    public static Event userRemoveLike(long userId, long filmId) {
        return addEvent(userId, filmId, EventType.LIKE, Operation.REMOVE);
    }

    public static Event userAddReview(long userId, long reviewId) {
        return addEvent(userId, reviewId, EventType.REVIEW, Operation.ADD);
    }

    public static Event userRemoveReview(long userID, long reviewId) {
        return addEvent(userID, reviewId, EventType.REVIEW, Operation.REMOVE);
    }

    public static Event userUpdateReview(long userId, long reviewId) {
        return addEvent(userId, reviewId, EventType.REVIEW, Operation.UPDATE);
    }

    public static Event userAddFriend(long userId, long friendId) {
        return addEvent(userId, friendId, EventType.FRIEND, Operation.ADD);
    }

    public static Event userRemoveFriend(long userId, long friendId) {
        return addEvent(userId, friendId, EventType.FRIEND, Operation.REMOVE);
    }

    private static Event addEvent(long userId, long entityId, EventType eventType, Operation operation) {
        return Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }
}
