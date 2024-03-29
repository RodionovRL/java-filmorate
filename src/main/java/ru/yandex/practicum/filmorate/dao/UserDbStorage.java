package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.exception.ErrorInsertToDbException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    static void checkUserIsExist(Long id, JdbcTemplate jdbcTemplate) {
        String sqlQuery = "SELECT ID FROM USERS WHERE ID = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, Long.class, id);
        } catch (EmptyResultDataAccessException e) {
            log.error("пользователь с запрошенным id {} не найден", id);
            throw new NotFoundException(String.format(
                    "пользователь с запрошенным id = %s не найден", id));
        }
    }

    @Override
    public User addUser(User newUser) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        newUser.setId(simpleJdbcInsert.executeAndReturnKey(newUser.toMap()).longValue());
        return newUser;
    }

    @Override
    public User updateUser(long id, User user) {
        String sqlQuery = "UPDATE USERS SET " +
                "NAME = ?, EMAIL = ?, LOGIN = ?, BIRTHDAY = ? " +
                "WHERE ID = ?";
        int numChanged = jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        if (numChanged == 0) {
            log.error("пользователь с запрошенным id {} не найден", id);
            throw new NotFoundException(String.format(
                    "пользователь с запрошенным id = %s не найден", id));
        }
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sqlQuery = "SELECT ID, NAME, EMAIL, LOGIN, BIRTHDAY " +
                "FROM USERS";
        return (jdbcTemplate.query(sqlQuery, this::userMapper));
    }

    @Override
    public User getUserById(Long id) {
        String sqlQuery = "SELECT ID, NAME, EMAIL, LOGIN, BIRTHDAY " +
                "FROM USERS WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::userMapper, id);
        } catch (RuntimeException e) {
            log.error("пользователь с запрошенным id {} не найден", id);
            throw new NotFoundException(String.format(
                    "пользователь с запрошенным id = %s не найден", id));
        }
    }

    @Override
    public boolean deleteUserById(Long id) {
        String sqlQuery = "DELETE FROM USERS WHERE ID = ?";
        log.info("удаляем пользователя id={}", id);
        return jdbcTemplate.update(sqlQuery, id) != 0;
    }

    @Override
    public boolean addFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            log.error("Нельзя добавить себя в друзья id=friendId={}", id);
            return false;
        }
        checkUserIsExist(friendId, jdbcTemplate);

        String sqlQuery = "MERGE INTO FRIENDS(USER_ID, FRIEND_ID) " +
                "VALUES (?, ?)";
        try {
            int result = jdbcTemplate.update(sqlQuery, id, friendId);
            return result > 0;
        } catch (RuntimeException e) {
            log.error("не удалось добавить id={} в друзья к id={}", friendId, id);
            throw new ErrorInsertToDbException(String.format(
                    "не удалось добавить id=%s в друзья к id=%s", friendId, id));
        }
    }

    @Override
    public List<User> getUsersFriends(Long id) {
        checkUserIsExist(id, jdbcTemplate);
        String sqlQuery = "SELECT * FROM USERS U " +
                "LEFT JOIN FRIENDS F ON U.ID = F.FRIEND_ID " +
                "WHERE F.USER_ID = ?";

        return (jdbcTemplate.query(sqlQuery, this::userMapper, id));
    }

    @Override
    public boolean deleteFriend(Long id, Long exFriendId) {
        checkUserIsExist(id, jdbcTemplate);
        checkUserIsExist(exFriendId, jdbcTemplate);
        String sqlQuery = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        if (jdbcTemplate.update(sqlQuery, id, exFriendId) == 0) {
            log.info("пользователь id={} не является другом id={}", exFriendId, id);
            return false;
        }
        return true;
    }

    @Override
    public List<Long> getUserRecommendations(long id) {
        checkUserIsExist(id, jdbcTemplate);

        String sqlQuery1 = "SELECT OL.USER_ID FROM LIKES UL " +
                "LEFT JOIN LIKES OL ON UL.FILM_ID = OL.FILM_ID AND UL.USER_ID != OL.USER_ID " +
                "WHERE UL.USER_ID = ? " +
                "GROUP BY OL.USER_ID " +
                "ORDER BY COUNT (OL.FILM_ID) DESC " +
                "LIMIT 1";

        Long recomendId = null;
        try {
            recomendId = jdbcTemplate.queryForObject(sqlQuery1, Long.class, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Пользователь с похожими вкусами для пользователя id={} не найден", id);
        }

        if (recomendId != null) {
            String sqlQuery2 = "SELECT OL.FILM_ID FROM LIKES OL " +
                    "WHERE OL.USER_ID = ? AND OL.FILM_ID NOT IN (" +
                    "SELECT UL.FILM_ID FROM LIKES UL " +
                    "WHERE UL.USER_ID = ?)";

            return jdbcTemplate.queryForList(sqlQuery2, Long.class, recomendId, id);
        }
        return Collections.emptyList();
    }

    private User userMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthday"))
                .build();
    }
}