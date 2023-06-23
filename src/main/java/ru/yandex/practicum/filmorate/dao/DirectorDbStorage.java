package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Primary
@Component
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query("SELECT * FROM director", Director::directorMapper);
    }

    @Override
    public Optional<Director> getDirectorById(int directorId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM director WHERE id = ?",
                    Director::directorMapper, directorId));

        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    @Override
    public Director addDirector(Director director) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("id");

        director.setId(insert.executeAndReturnKey(director.toMap()).intValue());

        return director;
    }

    @Override
    public Optional<Director> updateDirector(Director director) {
        int numChanged = jdbcTemplate.update("UPDATE director SET name = ? WHERE id = ?",
                director.getName(), director.getId());

        if (numChanged == 0) {
            log.warn("trying update non-existing director");
            return Optional.empty();
        }

        return Optional.of(director);
    }

    @Override
    public boolean deleteDirector(int directorId) {
        int numChanged = jdbcTemplate.update("DELETE FROM director WHERE id = ?", directorId);

        if (numChanged == 0) {
            log.warn("trying delete non-existing director");
            return false;
        }

        return true;
    }
}
