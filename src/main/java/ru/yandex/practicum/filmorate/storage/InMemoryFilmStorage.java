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
    @Getter
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public void put(int id, Film film) {
        films.put(id, film);
    }

    @Override
    public Film replace(int id, Film film) {

        if (!films.containsKey(film.getId())) {
            log.error("updateFilm: фильм с id={} не найден", film.getId());
            throw new FilmNotFoundException("updateFilm: фильм с запрошенным id не найден");
        }

        return films.replace(film.getId(), film);
    }

    @Override
    public Collection<Film> values() {
        return films.values();
    }

    @Override
    public Film getFilmById(int id) {

        return films.get(id);
    }
}
