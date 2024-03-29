package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.EventStorage;
import ru.yandex.practicum.filmorate.api.FilmStorage;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    @Autowired
    public UserService(UserStorage userStorage, FilmStorage filmStorage, EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
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
            eventStorage.addEvent(Event.userAddFriend(id, friendId));
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
            eventStorage.addEvent(Event.userRemoveFriend(id, exFriendId));
            return true;
        }
        return false;
    }

    public List<Film> getUserRecommendations(long id) {
        List<Long> recommendFilmIds = userStorage.getUserRecommendations(id);
        log.info("Получены id ({} шт.)  рекомендованных фильмов для пользователя с id={}", recommendFilmIds.size(), id);
        List<Film> recommendFilms = filmStorage.getFilmsByIds(recommendFilmIds);
        log.info("Возвращено {} рекомендаций для пользователя с id {}", recommendFilms.size(), id);
        return recommendFilms;
    }

    public List<Event> getFeedById(long userId) {
        return eventStorage.getLastEvents(userId);
    }

    private void checkName(@NotNull User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Поле name пустое, в качестве имени установлен login={}", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
