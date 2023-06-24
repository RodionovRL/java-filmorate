package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(int directorId) {
        Optional<Director> director = directorStorage.getDirectorById(directorId);

        if (director.isEmpty()) {
            throw new NotFoundException("director with id = " + directorId + " is not existing");
        }

        return director.get();
    }

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        Optional<Director> optionalDirector = directorStorage.updateDirector(director);

        if (optionalDirector.isEmpty()) {
            throw new NotFoundException("director with id = " + director.getId() + " is not existing");
        }

        return optionalDirector.get();
    }

    public boolean deleteDirector(int directorId) {
        if (!directorStorage.deleteDirector(directorId)) {
            throw new NotFoundException("director with id = " + directorId + " is not existing");
        }

        return true;
    }
}
