package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static int ids;
    @Getter
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        int id = getNewId();
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmIsContains(film.getId());
        return films.replace(film.getId(), film);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(int id) {
        checkFilmIsContains(id);
        return films.get(id);
    }

    private void checkFilmIsContains(Integer id) {
        if (!films.containsKey(id)) {
            log.error("updateFilm: фильм с id = {} не найден", id);
            throw new FilmNotFoundException("updateFilm: фильм с запрошенным id не найден");
        }
    }

    private int getNewId() {
        int newId = ++ids;
        log.trace("создан новый filmId = {}", newId);
        return newId;
    }
}
