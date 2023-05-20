package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private static int ids;
    FilmStorage films;
    UserService userService;

    @Autowired
    public FilmService(FilmStorage films, UserService userService) {
        this.films = films;
        this.userService = userService;
    }

    public Film addFilm(@NotNull Film film) {
        int id = getNewId();

        film.setId(id);
        films.put(id, film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    public Film updateFilm(@NotNull Film film) {

        Film oldFilm = films.replace(film.getId(), film);
        log.info("Информация о фильме {} изменена на {}", oldFilm, film);
        return film;
    }

    public Collection<Film> getAllFilms() {

        log.info("переданы все {} фильмов ", films.values().size());
        return films.values();
    }

    public Film getFilmById(int id) {
        Film film = films.getFilmById(id);
        if (film == null) {
            log.error("фильм с запрошенным id {} не найден", id);
            throw new FilmNotFoundException(String.format(
                    "фильм с запрошенным id = %s не найден", id));
        }
        return film;
    }

    public Film setLikeToFilm(Integer id, Integer userId) {
        userService.getUserById(userId);
        Film film = getFilmById(id);

        film.addLike(userId);
        log.info("фильму с id={} добавлен лайк пользователя с id={}", id, userId);
        return film;
    }

    public Film delLikeFromFilm(Integer id, Integer userId) {
        userService.getUserById(userId);
        Film film = getFilmById(id);
        film.delLike(userId);
        log.info("у фильма с id={} удалён лайк пользователя с id={}", id, userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> topFilms = getAllFilms().stream()
                .sorted(Film::compareByLikes)
                .limit(count)
                .collect(Collectors.toList());

        log.info("возвращён ТОП-{} фильмов: {}", count, topFilms );

        return topFilms;
    }

    private int getNewId() {
        int newId = ++ids;

        log.trace("создан новый filmId id={}", newId);
        return newId;
    }

}
