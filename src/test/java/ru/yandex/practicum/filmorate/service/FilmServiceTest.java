package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmServiceException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmServiceTest {
    FilmService filmService;
    Film film;

    @BeforeEach
    void setUp() {
        filmService = new FilmService();
        film = new Film(Integer.MAX_VALUE,
                "TestFilm",
                "TestFilm Description",
                LocalDate.of(2000, 1, 1),
                120);
    }

    @Test
    void shouldBeFilmServiceExceptionWhenUpdateFilmWithUnknownId() {
        FilmServiceException exception = Assertions.assertThrows(FilmServiceException.class,
                () -> filmService.updateFilm(film));

        assertEquals("updateFilm: фильм с запрошенным id не найден", exception.getMessage());
    }
}