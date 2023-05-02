package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.util.AfterInternationCinemaDayValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    private Validator validator;
    private Film validFilm;
    private Film invalidFilm;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        validFilm = Film.builder()
                .id(1)
                .name("validFilm")
                .description("validFilm duration")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();
    }

    @Test
    public void couldEmptyViolationsWhenValidFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);

        assertTrue(violations.isEmpty(), "Обнаружены ошибки: " + violations);
    }

    @Test
    public void couldRightErrorMessagesWhenNameIsNullDescriptionMore200SymbolsReleaseEarlyCinemaDayDurationIsNeg() {
        invalidFilm = validFilm;
        invalidFilm.setName(null);
        invalidFilm.setDescription("A".repeat(222));
        invalidFilm.setReleaseDate(AfterInternationCinemaDayValidator.INTERNATION_CINEMA_DAY.minusDays(1));
        invalidFilm.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        String[] expectedMessages = new String[]{"Название фильма не может быть пустым",
                "Не задано название фильма",
                "Описание должно быть не более 200 символов",
                "Дата релиза не может быть ранее 28 декабря 1895!",
                "Продолжительность фильма должна быть положительной"
        };

        String[] actualMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toArray(String[]::new);

        Arrays.sort(expectedMessages);
        Arrays.sort(actualMessages);

        assertFalse(violations.isEmpty(), "Ошибки не обнаружены");

        assertArrayEquals(expectedMessages, actualMessages);
    }

    @Test
    public void shouldRightErrorMessageWhenNameIsBlank() {
        invalidFilm = validFilm;
        invalidFilm.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(invalidFilm);
        String expectedMessage = "Название фильма не может быть пустым";

        String actualMessage = violations.iterator().next().getMessage();

        assertFalse(violations.isEmpty(), "Ошибки не обнаружены");

        assertEquals(expectedMessage, actualMessage);
    }
}