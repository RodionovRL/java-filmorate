package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.util.SearchBy;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static int ids;
    @Getter
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        long id = getNewId();
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        checkFilmIsContains(film.getId());
        return Optional.ofNullable(films.replace(film.getId(), film));
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(long id) {
        checkFilmIsContains(id);
        return films.get(id);
    }

    @Override
    public boolean deleteFilmById(Long id) {
        log.info("удаляем фильм id={}", id);
        return films.remove(id) != null;
    }

    @Override
    public boolean setLikeToFilm(Long filmId, Long userId) {
        getFilmById(filmId);
        return true;
    }

    @Override
    public boolean setMarkToFilm(Long filmId, Long userId, Integer mark) {
        return false;
    }

    @Override
    public boolean delLikeFromFilm(Long filmId, Long userId) {
        getFilmById(filmId);
        return true;
    }

    @Override
    public List<Film> getTopPopularFilms(int count, int genreId, int year) {
        return new ArrayList<>();
    }

    @Override
    public List<Genre> getAllGenres() {
        return new ArrayList<>();
    }

    @Override
    public Genre getGenreById(Integer id) {
        return new Genre(0, "xxx");
    }

    @Override
    public List<Mpa> getAllMpa() {
        return new ArrayList<>();
    }

    @Override
    public Mpa getMpaById(Integer id) {
        return new Mpa();
    }

    @Override
    public List<Film> getFilmsByDirector(long directorId, String param) {
        return new ArrayList<>();
    }

    @Override
    public List<Film> getFilmsByIds(List<Long> recommendFilmIds) {
        return new ArrayList<>();
    }

    @Override
    public List<Film> searchFilm(String query, SearchBy by) {
        return new ArrayList<>();
    }

    @Override
    public List<Film> getListCommonFilms(Long userId, Long friendId) {
        return new ArrayList<>();
    }

    private void checkFilmIsContains(Long id) {
        if (!films.containsKey(id)) {
            log.error("updateFilm: фильм с id = {} не найден", id);
            throw new NotFoundException("updateFilm: фильм с запрошенным id не найден");
        }
    }

    private int getNewId() {
        int newId = ++ids;
        log.trace("создан новый filmId = {}", newId);
        return newId;
    }
}
