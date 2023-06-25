package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService service;

    @GetMapping
    public List<Director> getAllDirectors() {
        log.info("GET: /directors");

        return service.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable @Positive int id) {
        log.info("GET: /directors/{}", id);

        return service.getDirectorById(id);
    }

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        log.info("POST: /directors - RequestBody director:{}", director);

        return service.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        return service.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteDirector(@PathVariable int id) {
        return new ResponseEntity<>(service.deleteDirector(id), HttpStatus.NO_CONTENT);
    }
}
