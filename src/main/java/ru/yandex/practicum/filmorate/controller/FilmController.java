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
        return new ResponseEntity<>(savedFilm, HttpStatus.OK);
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
}
