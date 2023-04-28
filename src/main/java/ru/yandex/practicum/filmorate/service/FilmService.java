package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmServiceException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FilmService {
    private static int ids;
    private final Map<Integer, Film> films = new HashMap<>();

    public Film addFilm(Film film) {
        int id = getNewId();
        film.setId(id);
        films.put(id, film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    public Film updateFilm(@NotNull Film film) {

        if (!films.containsKey(film.getId())) {
            log.error("updateFilm: фильм с id={} не найден", film.getId());
            throw new FilmServiceException("updateFilm: фильм с запрошенным id не найден");
        }
        Film oldFilm = films.replace(film.getId(), film);
        log.info("Информация о фильме {} изменена на {}", oldFilm, film);
        return film;
    }

    public Collection<Film> getAllFilms() {

        log.info("передан список всех фильмов {}", films.values());
        return films.values();
    }

    private int getNewId() {
        int newId = ++ids;
        log.trace("создан новый filmId id={}", newId);
        return newId;
    }
}
