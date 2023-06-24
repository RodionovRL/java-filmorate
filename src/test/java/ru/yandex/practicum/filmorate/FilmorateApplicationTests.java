package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;

    private final FilmDbStorage filmStorage;

    User user1;
    User user2;
    User user3;

    Film film1;
    Film film2;
    Film film3;

    List<Mpa> mpa;
    List<Genre> genres;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .id(0)
                .email("mail1@ya.ru")
                .login("login1")
                .name("name1")
                .birthday(Date.valueOf(LocalDate.of(1981, 1, 1)))
                .build();
        user2 = User.builder()
                .id(1)
                .email("mail2@ya.ru")
                .login("login2")
                .name("name2")
                .birthday(Date.valueOf(LocalDate.of(1982, 2, 2)))
                .build();
        user3 = User.builder()
                .id(2)
                .email("mail3@ya.ru")
                .login("login3")
                .name("name3")
                .birthday(Date.valueOf(LocalDate.of(1983, 3, 3)))
                .build();

        film1 = new Film(1,
                "film1",
                "description1",
                LocalDate.of(2001, 1, 1),
                110,
                new HashSet<>(),
                new Mpa(1, null),
                new HashSet<>());
        film2 = new Film(2,
                "film2",
                "description2",
                LocalDate.of(2002, 2, 2),
                111,
                new HashSet<>(),
                new Mpa(2, null),
                new HashSet<>());
        film3 = new Film(3,
                "film3",
                "description3",
                LocalDate.of(2003, 3, 3),
                112,
                new HashSet<>(),
                new Mpa(3, null),
                new HashSet<>());

        mpa = new ArrayList<>();
        mpa.add(new Mpa(1, "G"));
        mpa.add(new Mpa(2, "PG"));
        mpa.add(new Mpa(3, "PG-13"));
        mpa.add(new Mpa(4, "R"));
        mpa.add(new Mpa(5, "NC-17"));

        genres = new ArrayList<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));
        genres.add(new Genre(3, "Мультфильм"));
        genres.add(new Genre(4, "Триллер"));
        genres.add(new Genre(5, "Документальный"));
        genres.add(new Genre(6, "Боевик"));
    }

    @Test
    public void testAddUser() {
        assertEquals(userStorage.addUser(user1), (user1));
    }

    @Test
    public void testGetUserById() {
        userStorage.addUser(user1);
        User user = userStorage.getUserById(1L);

        assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testGetAllUsers() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        Collection<User> addedUsers = new ArrayList<>();
        addedUsers.add(user1);
        addedUsers.add(user2);
        addedUsers.add(user3);

        Collection<User> users = userStorage.getAllUsers();

        assertArrayEquals(addedUsers.toArray(), users.toArray());
    }

    @Test
    public void testGetEmptyAllUsers() {
        assertArrayEquals(new ArrayList<User>().toArray(), userStorage.getAllUsers().toArray());
    }

    @Test
    void testUpdateUser() {
        long id = userStorage.addUser(user1).getId();
        userStorage.updateUser(id, user2);
        User updatedUser = userStorage.getUserById(id);

        assertEquals(user2, updatedUser);
    }

    @Test
    void testAddFriend() {
        long id = userStorage.addUser(user1).getId();
        long friendId = userStorage.addUser(user2).getId();

        assertTrue(userStorage.addFriend(id, friendId));
        assertTrue(userStorage.getUsersFriends(id).contains(user2));
    }

    @Test
    void testGetAllFriendsAndDeleteFriend() {
        long id = userStorage.addUser(user1).getId();
        long friendId1 = userStorage.addUser(user2).getId();
        long friendId2 = userStorage.addUser(user3).getId();

        userStorage.addFriend(id, friendId1);
        userStorage.addFriend(id, friendId2);

        Collection<User> addedFriends = new ArrayList<>();
        addedFriends.add(user2);
        addedFriends.add(user3);

        assertArrayEquals(userStorage.getUsersFriends(id).toArray(), addedFriends.toArray());

        userStorage.deleteFriend(id, friendId1);

        assertFalse(userStorage.getUsersFriends(id).contains(user2));
        assertTrue(userStorage.getUsersFriends(id).contains(user3));
    }

    @Test
    void testAddFilm() {
        assertEquals(filmStorage.addFilm(film1), (film1));
    }

    @Test
    public void testGetFilmById() {
        filmStorage.addFilm(film1);
        Film film = filmStorage.getFilmById(1L);

        assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testGetAllFilms() {
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        Collection<Film> addedFilms = new ArrayList<>();
        addedFilms.add(film1);
        addedFilms.add(film2);
        addedFilms.add(film3);

        Collection<Film> films = filmStorage.getAllFilms();

        assertArrayEquals(addedFilms.toArray(), films.toArray());
    }

    @Test
    void testSetLikeToFilmAndDeleteLikes() {
        long user1Id = userStorage.addUser(user1).getId();
        long user2Id = userStorage.addUser(user2).getId();
        long user3Id = userStorage.addUser(user3).getId();

        long film1Id = filmStorage.addFilm(film1).getId();
        long film2Id = filmStorage.addFilm(film2).getId();
        long film3Id = filmStorage.addFilm(film3).getId();

        filmStorage.setLikeToFilm(film1Id, user1Id);
        filmStorage.setLikeToFilm(film1Id, user2Id);

        filmStorage.setLikeToFilm(film2Id, user1Id);

        filmStorage.setLikeToFilm(film3Id, user1Id);
        filmStorage.setLikeToFilm(film3Id, user2Id);
        filmStorage.setLikeToFilm(film3Id, user3Id);

        List<Film> requiredTop2afterAdd = new ArrayList<>();
        requiredTop2afterAdd.add(film3);
        requiredTop2afterAdd.add(film1);

        List<Film> top2 = filmStorage.getTopPopularFilms(2, 0, 0);

        assertArrayEquals(requiredTop2afterAdd.toArray(), top2.toArray());

        filmStorage.delLikeFromFilm(film2Id, user1Id);

        filmStorage.delLikeFromFilm(film3Id, user1Id);
        filmStorage.delLikeFromFilm(film3Id, user2Id);

        List<Film> requiredTop2afterDel = new ArrayList<>();
        requiredTop2afterDel.add(film1);
        requiredTop2afterDel.add(film3);

        top2 = filmStorage.getTopPopularFilms(2, 0, 0);

        assertArrayEquals(requiredTop2afterDel.toArray(), top2.toArray());
    }

    @Test
    void testGetAllMpaAndMpaById() {
        assertArrayEquals(mpa.toArray(), filmStorage.getAllMpa().toArray());
        assertEquals(filmStorage.getMpaById(1).getId(), 1);
        assertEquals(filmStorage.getMpaById(2).getId(), 2);
        assertEquals(filmStorage.getMpaById(3).getId(), 3);
        assertEquals(filmStorage.getMpaById(4).getId(), 4);
        assertEquals(filmStorage.getMpaById(5).getId(), 5);
        assertEquals(filmStorage.getMpaById(1).getName(), "G");
        assertEquals(filmStorage.getMpaById(2).getId(), 2);
        assertEquals(filmStorage.getMpaById(2).getName(), "PG");
        assertEquals(filmStorage.getMpaById(3).getName(), "PG-13");
        assertEquals(filmStorage.getMpaById(4).getName(), "R");
        assertEquals(filmStorage.getMpaById(5).getName(), "NC-17");
    }

    @Test
    void testGetAllGenreAndGenreById() {
        assertArrayEquals(genres.toArray(), filmStorage.getAllGenres().toArray());
        assertEquals(filmStorage.getGenreById(1).getId(), 1);
        assertEquals(filmStorage.getGenreById(2).getId(), 2);
        assertEquals(filmStorage.getGenreById(3).getId(), 3);
        assertEquals(filmStorage.getGenreById(4).getId(), 4);
        assertEquals(filmStorage.getGenreById(5).getId(), 5);
        assertEquals(filmStorage.getGenreById(6).getId(), 6);

        assertEquals(filmStorage.getGenreById(1).getName(), "Комедия");
        assertEquals(filmStorage.getGenreById(2).getName(), "Драма");
        assertEquals(filmStorage.getGenreById(3).getName(), "Мультфильм");
        assertEquals(filmStorage.getGenreById(4).getName(), "Триллер");
        assertEquals(filmStorage.getGenreById(5).getName(), "Документальный");
        assertEquals(filmStorage.getGenreById(6).getName(), "Боевик");
    }
}
