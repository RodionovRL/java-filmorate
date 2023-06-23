package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;
import java.util.stream.Collectors;

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
        Film film = getFilmById(filmId);
        if (film.addLike(userId)) {
            log.info("у фильма с id={} удалён лайк пользователя с id={}", filmId, userId);
            return true;
        }
        return false;
    }

    @Override
    public boolean delLikeFromFilm(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        if (film.delLike(userId)) {
            log.info("у фильма с id={} удалён лайк пользователя с id={}", filmId, userId);
            return true;
        }
        return false;
    }

    @Override
    public List<Film> getTopPopularFilms(int count) {
        List<Film> topFilms = getAllFilms().stream()
                .sorted(Film::compareByLikes)
                .limit(count)
                .collect(Collectors.toList());

        log.debug("возвращён ТОП-{} фильмов: {}", count, topFilms);
        return topFilms;
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

    private void checkFilmIsContains(Long id) {
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
