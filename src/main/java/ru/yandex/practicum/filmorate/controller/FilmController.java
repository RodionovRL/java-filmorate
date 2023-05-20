package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping()
    public ResponseEntity<Film> postFilm(@Valid @RequestBody Film film) {
        log.info("получен запрос на добавление фильма {}", film);
        Film savedFilm = filmService.addFilm(film);
        return new ResponseEntity<>(savedFilm, HttpStatus.CREATED);
    }

    @PutMapping("")
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("получен запрос на обновление фильма {}", film);
        Film updatedFilm = filmService.updateFilm(film);
        return new ResponseEntity<>(updatedFilm, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Collection<Film>> getAllFilms() {
        log.info("получен запрос на получение всех фильмов");
        Collection<Film> films = filmService.getAllFilms();
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(
            @PathVariable("id") Integer id) {
        log.info("получен запрос на получение фильма по id {}", id);
        Film film = filmService.getFilmById(id);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> setLikeToFilm(@PathVariable("id") Integer id,
                                              @PathVariable("userId") Integer userId) {
        log.info("получен запрос на на добавление фильму с id= {} лайка от пользователя с id= {}", id, userId);
        Film film = filmService.setLikeToFilm(id, userId);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> delLikeFromFilm(@PathVariable("id") Integer id,
                                                @PathVariable("userId") Integer userId) {
        log.info("получен запрос на на удаление у фильма с id= {} лайка пользователя с id= {}", id, userId);
        Film film = filmService.delLikeFromFilm(id, userId);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(
            @RequestParam(value = "count", required = false, defaultValue = "10") Integer count
    ) {
        log.info("получен запрос на получение ТОП{} популярных фильмов", count);
        List<Film> films = filmService.getPopularFilms(count);
        return new ResponseEntity<>(films, HttpStatus.OK);
    }


}
