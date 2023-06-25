package ru.yandex.practicum.filmorate.api;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(int directorId);

    Director addDirector(Director director);

    Optional<Director> updateDirector(Director director);

    boolean deleteDirector(int directorId);
}
