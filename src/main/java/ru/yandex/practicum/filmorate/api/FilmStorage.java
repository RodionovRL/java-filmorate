package ru.yandex.practicum.filmorate.api;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    Collection<Film> getAllFilms();

    Film getFilmById(long id);

    boolean setLikeToFilm(Long filmId, Long userId);

    boolean delLikeFromFilm(Long filmId, Long userId);

    List<Film> getTopPopularFilms(int count);

    List<Genre> getAllGenres();

    Genre getGenreById(Integer id);

    List<Mpa> getAllMpa();

    Mpa getMpaById(Integer id);

    List<Film> getFilmsByIds(Set<Long> recommendFilmIds);

    List<Film> getFilmsByDirector(long directorId);

    Collection<Film> getListCommonFilms(Long userId, Long friendId);
}
