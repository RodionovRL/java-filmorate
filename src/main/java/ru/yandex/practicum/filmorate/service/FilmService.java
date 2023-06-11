package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

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
        if (filmStorage.updateFilm(film).isEmpty()) {
            log.warn("фильм не найден {}", film);
            throw new FilmNotFoundException(String.format(
                    "фильм с запрошенным id = %s не найден", film.getId()));
        }
        log.info("Информация о фильме изменена {}", film);
        return film;
    }

    public Collection<Film> getAllFilms() {
        log.info("переданы все {} фильмов ", filmStorage.getAllFilms().size());
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public boolean setLikeToFilm(Long filmId, Long userId) {
        userStorage.getUserById(userId);
        return filmStorage.setLikeToFilm(filmId, userId);
    }

    public boolean delLikeFromFilm(Long filmId, Long userId) {
        userStorage.getUserById(userId);
        return filmStorage.delLikeFromFilm(filmId, userId);

    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getTopPopularFilms(count);
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        return filmStorage.getGenreById(id);
    }

    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Mpa getMpaById(Integer id) {
        return filmStorage.getMpaById(id);
    }
}
