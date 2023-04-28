package ru.yandex.practicum.filmorate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class UserServiceException extends RuntimeException {
    @Getter
    private String message;
}
