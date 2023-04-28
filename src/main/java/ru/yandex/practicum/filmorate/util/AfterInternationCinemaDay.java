package ru.yandex.practicum.filmorate.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = AfterInternationCinemaDayValidator.class)
@Documented

public @interface AfterInternationCinemaDay {
    String message() default "{Дата релиза не может быть ранее 28 декабря 1895}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
