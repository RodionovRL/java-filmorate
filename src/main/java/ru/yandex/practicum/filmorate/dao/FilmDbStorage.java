package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.util.SearchBy;

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
        setFilmDirector(newFilm);

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
        setFilmDirector(film);

        return Optional.of(film);
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION," +
                " F.MPA_ID,  M.NAME MPA_NAME " +
                "FROM FILM F " +
                "LEFT JOIN MPA M ON F.MPA_ID = M.ID";
        List<Film> films = (jdbcTemplate.query(sqlQuery, this::filmMapper));
        setGenresToFilms(films);
        setDirectorsToFilms(films);
        return films;
    }

    @Override
    public Film getFilmById(long id) {
        String sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION," +
                " F.MPA_ID, M.NAME MPA_NAME " +
                "FROM FILM F " +
                "LEFT JOIN MPA M ON F.MPA_ID = M.ID " +
                "WHERE F.ID = ?";
        try {
            Film film = (jdbcTemplate.queryForObject(sqlQuery, this::filmMapper, id));
            film.setGenres(getFilmsGenre(film.getId()));
            film.setDirectors(getDirectors(film.getId()));
            return film;
        } catch (RuntimeException e) {
            log.error("фильм с запрошенным id {} не найден", id);
            throw new NotFoundException(String.format(
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
    public List<Film> getTopPopularFilms(int count, int genreId, int year) {
        List<Film> films;
        if (genreId > 0 && year == -1) {
            String sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, " +
                    "F.DURATION, F.MPA_ID, M.NAME AS MPA_NAME, COUNT(L.USER_ID) RATE " +
                    "FROM FILM F " +
                    "LEFT JOIN MPA M ON F.MPA_ID = M.ID " +
                    "LEFT JOIN LIKES L on F.ID = L.FILM_ID " +
                    "LEFT JOIN FILM_GENRE FG on F.ID = FG.FILM_ID " +
                    "WHERE FG.GENRE_ID = ? " +
                    "GROUP BY F.ID, FG.GENRE_ID " +
                    "ORDER BY RATE DESC " +
                    "LIMIT ? ";
            log.debug("возвращён ТОП-{} фильмов, жанра-{}", count, genreId);
            films = jdbcTemplate.query(sqlQuery, this::filmMapper, genreId, count);
        } else if (genreId == -1 && year > 0) {
            String sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, " +
                    "F.DURATION, F.MPA_ID, M.NAME AS MPA_NAME, COUNT(L.USER_ID) RATE " +
                    "FROM FILM F " +
                    "LEFT JOIN MPA M ON F.MPA_ID = M.ID " +
                    "LEFT JOIN LIKES L on F.ID = L.FILM_ID " +
                    "WHERE EXTRACT(YEAR FROM RELEASE_DATE) = ? " +
                    "GROUP BY F.ID " +
                    "ORDER BY RATE DESC " +
                    "LIMIT ? ";
            log.debug("возвращён ТОП-{} фильмов {} года", count, year);
            films = jdbcTemplate.query(sqlQuery, this::filmMapper, year, count);
        } else if (genreId > 0 && year > 0) {
            String sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, " +
                    "F.DURATION, F.MPA_ID, M.NAME AS MPA_NAME, COUNT(L.USER_ID) RATE " +
                    "FROM FILM F " +
                    "LEFT JOIN MPA M ON F.MPA_ID = M.ID " +
                    "LEFT JOIN LIKES L on F.ID = L.FILM_ID " +
                    "LEFT JOIN FILM_GENRE FG on F.ID = FG.FILM_ID " +
                    "WHERE FG.GENRE_ID = ? " +
                    "AND EXTRACT(YEAR FROM RELEASE_DATE) = ? " +
                    "GROUP BY F.ID,  FG.GENRE_ID " +
                    "ORDER BY RATE DESC " +
                    "LIMIT ?";
            log.debug("возвращён ТОП-{} фильмов, жанра-{}, {} года", count, genreId, year);
            films = jdbcTemplate.query(sqlQuery, this::filmMapper, genreId, year, count);
        } else {
            String sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, " +
                    "F.DURATION, F.MPA_ID, M.NAME AS MPA_NAME, COUNT(L.USER_ID) RATE " +
                    "FROM FILM F " +
                    "LEFT JOIN MPA M ON F.MPA_ID = M.ID " +
                    "LEFT JOIN LIKES L on F.ID = L.FILM_ID " +
                    "GROUP BY F.ID " +
                    "ORDER BY RATE DESC " +
                    "LIMIT ? ";
            log.debug("возвращён ТОП-{} фильмов, без параметров", count);
            films = jdbcTemplate.query(sqlQuery, this::filmMapper, count);
        }
        setGenresToFilms(films);
        setDirectorsToFilms(films);
        return films;
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
            throw new NotFoundException(String.format(
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
            throw new NotFoundException(String.format(
                    "mpa с запрошенным id = %s не найден", id));
        }
    }

    @Override
    public List<Film> getFilmsByIds(Set<Long> filmIds) {

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        String sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION," +
                " F.MPA_ID,  M.NAME MPA_NAME " +
                "FROM FILM F LEFT JOIN MPA M ON F.MPA_ID = M.ID WHERE F.ID IN (%s) " +
                "ORDER BY F.ID";

        List<Film> films = jdbcTemplate.query(String.format(sqlQuery, inSql), this::filmMapper, filmIds.toArray());
        setGenresToFilms(films);
        setDirectorsToFilms(films);
        return films;
    }

    @Override
    public List<Film> getFilmsByDirector(long directorId) {
        String sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION," +
                " F.MPA_ID,  M.NAME MPA_NAME " +
                "FROM FILM F " +
                "LEFT JOIN MPA M ON F.MPA_ID = M.ID " +
                "INNER JOIN film_director fd on f.id = fd.FILM_ID " +
                "WHERE fd.DIRECTOR_ID = ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::filmMapper, directorId);
        setGenresToFilms(films);
        setDirectorsToFilms(films);
        return films;
    }

    @Override
    public List<Film> searchFilm(String query, SearchBy by) {
        List<Film> films;
        String sqlQuery = "";
        log.info("запрос в БД на возврат фильмов где {} содержат {}", by, query);
        if (by.equals(SearchBy.TITLE_DIRECTOR) || by.equals(SearchBy.DIRECTOR_TITLE)) {
            sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION," +
                    "F.MPA_ID,  M.NAME MPA_NAME, " +
                    "COUNT(L.USER_ID) LiKES " +
                    "FROM FILM F " +
                    "LEFT JOIN MPA M ON F.MPA_ID = M.ID " +
                    "LEFT JOIN FILM_DIRECTOR FD on F.ID = FD.FILM_ID " +
                    "LEFT JOIN DIRECTOR D on D.ID = FD.DIRECTOR_ID " +
                    "LEFT JOIN LIKES L on F.ID = L.FILM_ID " +
                    "WHERE LCASE(F.NAME) LIKE LCASE(?) OR LCASE(D.NAME) LIKE LCASE(?)" +
                    "GROUP BY F.ID " +
                    "ORDER BY LiKES DESC";
            films = (jdbcTemplate.query(sqlQuery, this::filmMapper, "%" + query + "%", "%" + query + "%"));
        } else {
            if (by.equals(SearchBy.TITLE)) {
                sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION," +
                        " F.MPA_ID,  M.NAME MPA_NAME, " +
                        "COUNT(L.USER_ID) LiKES  " +
                        "FROM FILM F " +
                        "LEFT JOIN MPA M ON F.MPA_ID = M.ID " +
                        "LEFT JOIN LIKES L on F.ID = L.FILM_ID " +
                        "WHERE LCASE(F.NAME) LIKE LCASE(?) " +
                        "GROUP BY F.ID " +
                        "ORDER BY LiKES DESC";
            } else if (by.equals(SearchBy.DIRECTOR)) {
                sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION," +
                        " F.MPA_ID,  M.NAME MPA_NAME, " +
                        "COUNT(L.USER_ID) LiKES " +
                        "FROM FILM F " +
                        "LEFT JOIN MPA M ON F.MPA_ID = M.ID " +
                        "LEFT JOIN FILM_DIRECTOR FD on F.ID = FD.FILM_ID " +
                        "LEFT JOIN DIRECTOR D on D.ID = FD.DIRECTOR_ID " +
                        "LEFT JOIN LIKES L on F.ID = L.FILM_ID " +
                        "WHERE LCASE(D.NAME) LIKE LCASE(?) " +
                        "GROUP BY F.ID " +
                        "ORDER BY LiKES DESC";
            }
            films = jdbcTemplate.query(sqlQuery, this::filmMapper, "%" + query + "%");
        }
        setGenresToFilms(films);
        setDirectorsToFilms(films);
        return films;
    }

    @Override
    public List<Film> getListCommonFilms(Long userId, Long friendId) {
        String sqlQuery = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID, M.NAME MPA_NAME " +
                "FROM FILM F " +
                "LEFT JOIN MPA M ON F.MPA_ID = M.ID " +
                "WHERE F.ID IN (SELECT FILM_ID " +
                "FROM LIKES " +
                "WHERE USER_ID = ? " +
                "OR USER_ID = ? " +
                "GROUP BY FILM_ID " +
                "HAVING Count(FILM_ID) >1 " +
                "ORDER BY count(USER_ID) DESC) ";
        List<Film> films = (jdbcTemplate.query(sqlQuery, this::filmMapper, userId, friendId));
        setGenresToFilms(films);
        setDirectorsToFilms(films);
        return films;
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

    private void setFilmDirector(Film film) {
        String sqlQuery = "DELETE FROM film_director WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());

        if (Objects.isNull(film.getDirectors()) || film.getDirectors().size() == 0) {
            return;
        }

        sqlQuery = "INSERT INTO FILM_DIRECTOR(FILM_ID, DIRECTOR_ID) VALUES ( ?, ? )";
        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(sqlQuery, film.getId(), director.getId());
        }

    }

    private Film filmMapper(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name"));

        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpa)
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

    private Set<Director> getDirectors(long filmId) {
        String sqlQuery = "SELECT FD.DIRECTOR_ID ID, D.NAME " +
                "FROM FILM_DIRECTOR FD " +
                "INNER JOIN DIRECTOR D ON FD.DIRECTOR_ID = D.ID " +
                "WHERE FD.FILM_ID = ? " +
                "ORDER BY ID";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, Director::directorMapper, filmId));
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

    private void setGenresToFilms(List<Film> films) {
        Map<Long, Set<Genre>> allFilmsGenres = new HashMap<>();
        films.forEach(f -> f.setGenres(new HashSet<>()));
        List<Long> filmsIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        String inSql = String.join(", ", Collections.nCopies(filmsIds.size(), "?"));

        String sqlQuery = String.format("SELECT FG.FILM_ID, FG.GENRE_ID, G.NAME " +
                "FROM FILM_GENRE FG " +
                "JOIN GENRE G ON FG.GENRE_ID = G.ID " +
                "WHERE FG.FILM_ID IN (%s)", inSql);

        jdbcTemplate.query(sqlQuery, rs -> {
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
            long filmId = rs.getLong("film_id");
            allFilmsGenres.putIfAbsent(filmId, new HashSet<>());
            allFilmsGenres.get(filmId).add(genre);
        }, filmsIds.toArray());
        films.stream()
                .peek(f -> f.setGenres(new HashSet<>()))
                .filter(f -> allFilmsGenres.containsKey(f.getId()))
                .forEach(f -> f.setGenres(allFilmsGenres.get(f.getId())));
    }

    private void setDirectorsToFilms(List<Film> films) {
        Map<Long, Set<Director>> allFilmsDirectors = new HashMap<>();
        films.forEach(f -> f.setDirectors(new HashSet<>()));
        List<Long> filmsIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        String inSql = String.join(", ", Collections.nCopies(filmsIds.size(), "?"));

        String sqlQuery = String.format("SELECT FG.FILM_ID, FG.DIRECTOR_ID, G.NAME " +
                "FROM FILM_DIRECTOR FG " +
                "JOIN DIRECTOR G ON FG.DIRECTOR_ID = G.ID " +
                "WHERE FG.FILM_ID IN (%s)", inSql);

        jdbcTemplate.query(sqlQuery, rs -> {
            Director director = new Director(rs.getLong("director_id"), rs.getString("name"));
            long filmId = rs.getLong("film_id");
            allFilmsDirectors.putIfAbsent(filmId, new HashSet<>());
            allFilmsDirectors.get(filmId).add(director);
        }, filmsIds.toArray());
        films.stream()
                .peek(f -> f.setDirectors(new HashSet<>()))
                .filter(f -> allFilmsDirectors.containsKey(f.getId()))
                .forEach(f -> f.setDirectors(allFilmsDirectors.get(f.getId())));
    }
}