package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.EventStorage;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.util.SearchBy;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, EventStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    public Film addFilm(Film film) {
        Film addedFilm = filmStorage.addFilm(film);
        log.info("Добавлен фильм {}", addedFilm);
        return addedFilm;
    }

    public Film updateFilm(Film film) {
        if (filmStorage.updateFilm(film).isEmpty()) {
            log.warn("фильм не найден {}", film);
            throw new NotFoundException(String.format(
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
        if (filmStorage.setLikeToFilm(filmId, userId)) {
            eventStorage.addEvent(Event.userAddLike(userId, filmId));
            return true;
        }
        return false;
    }

    public boolean setMarkToFilm(Long filmId, Long userId, Integer mark) {
        userStorage.getUserById(userId);
        if (filmStorage.setMarkToFilm(filmId, userId, mark)) {
            eventStorage.addEvent(Event.userAddLike(userId, filmId));
            return true;
        }
        return false;
    }

    public boolean delLikeFromFilm(Long filmId, Long userId) {
        userStorage.getUserById(userId);
        if (!filmStorage.delLikeFromFilm(filmId, userId)) {
            eventStorage.addEvent(Event.userRemoveLike(userId, filmId));
            return true;
        }
        return false;
    }

    public List<Film> getPopularFilms(int count, int genreId, int year) {
        return filmStorage.getTopPopularFilms(count, genreId, year);
    }

    public List<Film> searchFilm(String query, SearchBy by) {
        return filmStorage.searchFilm(query, by);
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

    public boolean deleteFilmById(Long id) {
        return filmStorage.deleteFilmById(id);
    }

    public List<Film> getSortedFilms(String param, long directorId) {
        List<Film> films = filmStorage.getFilmsByDirector(directorId, param);

        if (films.isEmpty()) {
            throw new NotFoundException("wrong director_id");
        }

        return films;
    }

    public List<Film> getListCommonFilms(Long userId, Long friendId) {
        return filmStorage.getListCommonFilms(userId, friendId);
    }
}
