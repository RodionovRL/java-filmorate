package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public User addUser(User newUser) {
        checkName(newUser);
        User addedUser = userStorage.addUser(newUser);

        log.info("Добавлен пользователь {}", addedUser);
        return addedUser;
    }

    public User updateUser(User user) {
        checkName(user);
        User oldUser = userStorage.updateUser(user.getId(), user);
        log.info("Информация о пользователе {} изменена на {}", oldUser, user);
        return user;
    }

    public Collection<User> getAllUsers() {
        log.info("переданы все {} пользователей", userStorage.getAllUsers().size());
        return userStorage.getAllUsers();
    }

    public boolean deleteUserById(Long id) {
        return userStorage.deleteUserById(id);
    }

    public boolean addFriend(Long id, Long friendId) {
        if (userStorage.addFriend(id, friendId)) {
            log.info("Пользователь id={} добавил в друзья пользователя id={}", id, friendId);
            return true;
        }
        return false;
    }

    public List<User> getAllUsersFriends(Long id) {
        return userStorage.getUsersFriends(id);
    }

    public List<User> getCommonFriend(Long id, Long otherId) {
        List<User> userFriends = getAllUsersFriends(id);
        List<User> otherUserFriends = getAllUsersFriends(otherId);

        List<User> commonFriends = userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());

        log.info("Возвращены общие друзья пользователя с id={} и пользователя с id={} :{}", id, otherId, commonFriends);

        return commonFriends;
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public boolean deleteUsersFriend(Long id, Long exFriendId) {
        if (userStorage.deleteFriend(id, exFriendId)) {
            log.info("User id={} и User id={} больше не друзья", id, exFriendId);
            return true;
        }
        return false;
    }

    public Collection<Film> getUserRecommendations(long id) {
        Set<Long> recommendFilmIds = userStorage.getUserRecommendations(id);
        log.info("Получены id рекомендованных фильмов для пользователя с id={}", id);
        Collection<Film> recommendFilms = filmStorage.getFilmsByIds(recommendFilmIds);
        log.info("Возвращены рекомендации фильмов для пользователя с id={}", id);
        return recommendFilms;
    }

    private void checkName(@NotNull User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("Поле name пустое, в качестве имени установлен login=\"{}\"", user.getLogin());
            user.setName(user.getLogin());
        }
    }


}
