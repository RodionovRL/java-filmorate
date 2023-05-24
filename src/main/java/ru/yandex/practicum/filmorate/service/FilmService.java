package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    FilmStorage filmStorage;
    UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {

        Film addedFilm = filmStorage.addFilm(film);
        log.info("Добавлен фильм {}", addedFilm);
        return addedFilm;
    }

    public Film updateFilm(Film film) {

        Film oldFilm = filmStorage.updateFilm(film);
        log.info("Информация о фильме {} изменена на {}", oldFilm, film);
        return film;
    }

    public Collection<Film> getAllFilms() {

        log.info("переданы все {} фильмов ", filmStorage.getAllFilms().size());
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.error("фильм с запрошенным id {} не найден", id);
            throw new FilmNotFoundException(String.format(
                    "фильм с запрошенным id = %s не найден", id));
        }
        return film;
    }

    public Film setLikeToFilm(Integer id, Integer userId) {
        userStorage.getUserById(userId);
        Film film = getFilmById(id);

        film.addLike(userId);
        log.info("фильму с id={} добавлен лайк пользователя с id={}", id, userId);
        return film;
    }

    public Film delLikeFromFilm(Integer id, Integer userId) {
        userStorage.getUserById(userId);
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

        log.info("возвращён ТОП-{} фильмов: {}", count, topFilms);

        return topFilms;
    }


}
