package ru.yandex.practicum.filmorate.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    void put(int id, Film film);

    Film replace(int id, Film film);

    Collection<Film> values();

    Film getFilmById(int id);
}
