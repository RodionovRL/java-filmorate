package ru.yandex.practicum.filmorate.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ReviewNotFoundException extends RuntimeException {
	 @Getter
	private String message;
}