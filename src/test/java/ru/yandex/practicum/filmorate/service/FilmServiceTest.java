package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmServiceException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    FilmService filmService;
    Film film;
    String filmName;
    String filmDescription;
    LocalDate filmReleaseDate;
    int filmDuration;

    @BeforeEach
    void setUp() {
        filmService = new FilmService();
        filmName = "TestFilm";
        filmDescription = "TestFilm Description";
        filmReleaseDate = LocalDate.of(2000, 1, 1);
        filmDuration = 120;
        film = Film.builder()
                .id(Integer.MAX_VALUE)
                .name(filmName)
                .description(filmDescription)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .build();
    }

    @Test
    void testAddFilm() {
        Film savedFilm = filmService.addFilm(film);

        assertTrue(filmService.getAllFilms().contains(savedFilm), "film не добавлен в память");
    }

    @Test
    void testUpdateFilm() {

        String filmForUpdateName = "UpdatedTestFilm";
        String filmForUpdateDescription = "Updated TestFilm Description";
        LocalDate filmForUpdateReleaseDate = LocalDate.of(2010, 1, 1);
        int filmForUpdateDuration = 180;

        int id = filmService.addFilm(film).getId();

        Film filmForUpdate = new Film(id,
                filmForUpdateName,
                filmForUpdateDescription,
                filmForUpdateReleaseDate,
                filmForUpdateDuration);

        filmService.updateFilm(filmForUpdate);

        Film updatedFilm = filmService.getAllFilms().iterator().next();

        assertAll("Film fields",
                () -> assertEquals(filmForUpdate.getName(), updatedFilm.getName(), "name не совпадает"),
                () -> assertEquals(filmForUpdate.getDescription(), updatedFilm.getDescription(), "description не совпадает"),
                () -> assertEquals(filmForUpdate.getReleaseDate(), updatedFilm.getReleaseDate(), "releaseDate не совпадает"),
                () -> assertEquals(filmForUpdate.getDuration(), updatedFilm.getDuration(), "duration не совпадает"),
                () -> assertEquals(filmForUpdate, updatedFilm, "метод вернул другой объект")
        );
    }

    @Test
    void shouldBeFilmServiceExceptionWhenUpdateFilmWithUnknownId() {
        FilmServiceException exception = Assertions.assertThrows(FilmServiceException.class,
                () -> filmService.updateFilm(film));

        assertEquals("updateFilm: фильм с запрошенным id не найден", exception.getMessage());
    }

    @Test
    void testGetAllFilms() {
        Film film1 = new Film(1,
                "film1",
                "description1",
                LocalDate.of(2001, 1, 1),
                110);
        Film film2 = new Film(1,
                "film2",
                "description2",
                LocalDate.of(2001, 1, 2),
                120);
        Film film3 = new Film(1,
                "film3",
                "description3",
                LocalDate.of(2001, 1, 3),
                130);
        Film film4 = new Film(1,
                "film4",
                "description4",
                LocalDate.of(2001, 1, 4),
                140);
        Film film5 = new Film(1,
                "film5",
                "description5",
                LocalDate.of(2001, 1, 5),
                150);

        List<Film> testFilms = new ArrayList<>();

        testFilms.add(film);
        testFilms.add(film1);
        testFilms.add(film2);
        testFilms.add(film3);
        testFilms.add(film4);
        testFilms.add(film5);

        filmService.addFilm(film);
        filmService.addFilm(film1);
        filmService.addFilm(film2);
        filmService.addFilm(film3);
        filmService.addFilm(film4);
        filmService.addFilm(film5);

        assertArrayEquals(testFilms.toArray(), filmService.getAllFilms().toArray());
    }
}