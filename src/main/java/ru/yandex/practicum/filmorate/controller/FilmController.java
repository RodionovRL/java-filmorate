package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.util.SearchBy;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping()
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public ResponseEntity<Film> postFilm(@Valid @RequestBody Film film) {
        log.info("получен запрос на добавление фильма {}", film);
        Film savedFilm = filmService.addFilm(film);
        return new ResponseEntity<>(savedFilm, HttpStatus.CREATED);
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("получен запрос на обновление фильма {}", film);
        Film updatedFilm = filmService.updateFilm(film);
        return new ResponseEntity<>(updatedFilm, HttpStatus.OK);
    }

    @GetMapping("/films")
    public ResponseEntity<Collection<Film>> getAllFilms() {
        log.info("получен запрос на получение всех фильмов");
        Collection<Film> films = filmService.getAllFilms();
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping("/films/{id}")
    public ResponseEntity<Film> getFilmById(
            @PathVariable("id") Integer id) {
        log.info("получен запрос на получение фильма по id {}", id);
        Film film = filmService.getFilmById(id);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @DeleteMapping("/films/{id}")
    public ResponseEntity<Boolean> deleteFilmById(@PathVariable("id") Long id) {
        log.info("получен запрос на на удаление фильма id={}", id);
        boolean result = filmService.deleteFilmById(id);
        if (!result) {
            log.warn("Attempt to delete nonexistent film id={}", id);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public ResponseEntity<Boolean> setLikeToFilm(@PathVariable("id") Long id,
                                                 @PathVariable("userId") Long userId) {
        log.info("получен запрос на на добавление фильму с id= {} лайка от пользователя с id= {}", id, userId);
        return new ResponseEntity<>(filmService.setLikeToFilm(id, userId), HttpStatus.OK);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public ResponseEntity<Boolean> delLikeFromFilm(@PathVariable("id") Long id,
                                                   @PathVariable("userId") Long userId) {
        log.info("получен запрос на на удаление у фильма с id= {} лайка пользователя с id= {}", id, userId);
        return new ResponseEntity<>(filmService.delLikeFromFilm(id, userId), HttpStatus.OK);
    }

    @GetMapping(value = "/films/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(defaultValue = "10") Integer count,
                                                      @RequestParam(defaultValue = "-1") Integer genreId,
                                                      @RequestParam(defaultValue = "-1") Integer year) {
        List<Film> films = filmService.getPopularFilms(count, genreId, year);
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping("/films/search")
    public ResponseEntity<List<Film>> findFilms(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "by") SearchBy by
    ) {
        log.info("Запрос на поиск по строке {}, встречающейся в {}", query, by);
        List<Film> films = filmService.searchFilm(query, by);
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping("/genres")
    public ResponseEntity<List<Genre>> getAllGenres() {
        log.info("получен запрос на получение всех жанров");
        List<Genre> genres = filmService.getAllGenres();
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }

    @GetMapping("/genres/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable Integer id) {
        log.info("получен запрос на получение жанра id={}", id);
        Genre genre = filmService.getGenreById(id);
        return new ResponseEntity<>(genre, HttpStatus.OK);
    }

    @GetMapping("/mpa")
    public ResponseEntity<List<Mpa>> getAllMpa() {
        log.info("получен запрос на получение всех mpa");
        List<Mpa> mpaList = filmService.getAllMpa();
        return new ResponseEntity<>(mpaList, HttpStatus.OK);
    }

    @GetMapping("/mpa/{id}")
    public ResponseEntity<Mpa> getMpaById(@PathVariable Integer id) {
        log.info("получен запрос на получение mpa id={}", id);
        Mpa mpa = filmService.getMpaById(id);
        return new ResponseEntity<>(mpa, HttpStatus.OK);
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getSortedFilms(@RequestParam(value = "sortBy") String param,
                                     @PathVariable @Positive long directorId) {

        return filmService.getSortedFilms(param, directorId);
    }

    @GetMapping("/films/common")
    public List<Film> getListCommonFilms(@RequestParam Long userId, Long friendId) {
        log.info("получен запрос на получение списка общих фильмов пользователя id={} и id={}", userId, friendId);
        return filmService.getListCommonFilms(userId, friendId);
    }
}
