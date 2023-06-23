package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film newFilm) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");

        newFilm.setId(simpleJdbcInsert.executeAndReturnKey(newFilm.toMap()).longValue());

        setMpaName(newFilm);
        setGenreName(newFilm);
        setFilmGenreInBd(newFilm);

        return newFilm;
    }


    @Override
    public Optional<Film> updateFilm(Film film) {
        String sqlQuery = "UPDATE FILM SET " +
                "NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "WHERE ID = ?";
        int numChanged = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (numChanged == 0) {
            log.error("фильм с запрошенным id {} не найден", film.getId());
            return Optional.empty();
        }

        setMpaName(film);
        setGenreName(film);
        setFilmGenreInBd(film);

        return Optional.of(film);
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION," +
                " F.MPA_ID,  M.NAME MPA_NAME " +
                "FROM FILM F " +
                "LEFT JOIN MPA M ON F.MPA_ID = M.ID";
        return (jdbcTemplate.query(sqlQuery, this::filmMapper));
    }

    @Override
    public Film getFilmById(long id) {
        String sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION," +
                " F.MPA_ID, M.NAME MPA_NAME " +
                "FROM FILM F " +
                "LEFT JOIN MPA M ON F.MPA_ID = M.ID " +
                "WHERE F.ID = ?";
        try {
            return (jdbcTemplate.queryForObject(sqlQuery, this::filmMapper, id));
        } catch (RuntimeException e) {
            log.error("фильм с запрошенным id {} не найден", id);
            throw new FilmNotFoundException(String.format(
                    "фильм с запрошенным id = %s не найден", id));
        }
    }


    @Override
    public boolean deleteFilmById(Long id) {
        String sqlQuery = "DELETE FROM FILM WHERE ID = ?";
        log.info("удаляем фильм id={}", id);
        return jdbcTemplate.update(sqlQuery, id) != 0;
    }

    @Override
    public boolean setLikeToFilm(Long filmId, Long userId) {
        UserDbStorage.checkUserIsExist(userId, jdbcTemplate);
        Film film = getFilmById(filmId);
        film.addLike(userId);
        String sqlQuery = "MERGE INTO LIKES " +
                "(FILM_ID, USER_ID)" +
                "VALUES (?, ?)";
        int numChanged = jdbcTemplate.update(sqlQuery,
                filmId,
                userId);
        return numChanged > 0;
    }

    @Override
    public boolean delLikeFromFilm(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        film.delLike(userId);
        String sqlQuery = "DELETE FROM LIKES " +
                "WHERE FILM_ID = ? AND USER_ID = ?";
        int numChanged = jdbcTemplate.update(sqlQuery,
                filmId,
                userId);
        log.info("у фильма с id={} удалён лайк пользователя с id={}", filmId, userId);
        return numChanged == 0;
    }

    @Override
    public List<Film> getTopPopularFilms(int count) {
        String sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION," +
                " F.MPA_ID,  M.NAME AS MPA_NAME,  COUNT(L.FILM_ID) RATE " +
                "FROM FILM F " +
                "LEFT JOIN MPA M ON F.MPA_ID = M.ID " +
                "LEFT JOIN LIKES L ON F.ID = L.FILM_ID " +
                "GROUP BY F.ID, F.NAME ORDER BY RATE DESC " +
                "LIMIT ?";

        log.debug("возвращён ТОП-{} фильмов", count);
        return jdbcTemplate.query(sqlQuery, this::filmMapper, count);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT ID, NAME " +
                "FROM GENRE " +
                "ORDER BY ID";
        return (jdbcTemplate.query(sqlQuery, this::genreMapper));
    }

    @Override
    public Genre getGenreById(Integer id) {
        String sqlQuery = "SELECT ID, NAME " +
                "FROM GENRE " +
                "WHERE ID=?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::genreMapper, id);
        } catch (RuntimeException e) {
            log.error("жанр с запрошенным id {} не найден", id);
            throw new GenreNotFoundException(String.format(
                    "жанр с запрошенным id = %s не найден", id));
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT ID, NAME " +
                "FROM MPA " +
                "ORDER BY ID";
        return jdbcTemplate.query(sqlQuery, this::mpaMapper);
    }

    @Override
    public Mpa getMpaById(Integer id) {
        String sqlQuery = "SELECT ID, NAME " +
                "FROM MPA " +
                "WHERE ID=?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mpaMapper, id);
        } catch (RuntimeException e) {
            log.error("mpa с запрошенным id {} не найден", id);
            throw new GenreNotFoundException(String.format(
                    "mpa с запрошенным id = %s не найден", id));
        }
    }

    private void setGenreName(Film newFilm) {
        newFilm.setGenres(newFilm.getGenres().stream()
                .map(g -> getGenreById(g.getId()))
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    private void setFilmGenreInBd(Film film) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        if (film.getGenres() == null || film.getGenres().size() == 0) {
            return;
        }

        sqlQuery = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) " +
                "VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
        }
    }

    private Film filmMapper(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name"));
        Set<Genre> genres = getFilmsGenre(resultSet.getLong("id"));
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpa)
                .genres(genres)
                .build();
    }

    private Set<Genre> getFilmsGenre(long id) {
        String sqlQuery = "SELECT FG.GENRE_ID AS ID, G.NAME GENRE_NAME " +
                "FROM FILM_GENRE FG " +
                "LEFT JOIN GENRE G ON FG.GENRE_ID = G.ID " +
                "WHERE FG.FILM_ID = ? " +
                "ORDER BY ID";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::genreMapper, id));
    }

    private Genre genreMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    private Mpa mpaMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    private void setMpaName(Film film) {
        int mpaId = film.getMpa().getId();
        Mpa mpa = getMpaById(mpaId);
        film.setMpa(mpa);
    }
}