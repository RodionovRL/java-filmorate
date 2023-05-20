package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private static int ids;
    UserStorage users;

    @Autowired
    public UserService(UserStorage users) {
        this.users = users;
    }

    public User addUser(@NotNull User newUser) {
        int id = getNewId();

        newUser.setId(id);
        checkName(newUser);
        users.put(id, newUser);
        log.info("Добавлен пользователь {}", newUser);
        return newUser;
    }

    public User updateUser(@NotNull User user) {

        checkName(user);
        User oldUser = users.replace(user.getId(), user);
        if (oldUser == null) {
            log.error("updateUser: пользователь с id={} не найден", user.getId());
            throw new UserNotFoundException(String.format("updateUser:  не найден пользователь %s", user));
        }
        log.info("Информация о пользователе {} изменена на {}", oldUser, user);
        return user;
    }

    public Collection<User> getAllUsers() {

        log.info("переданы все {} пользователей", users.values().size());
        return users.values();
    }

    public void addFriend(Integer id, Integer friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        user.addFriend(friendId);
        friend.addFriend(id);

        log.info("User {} и User {} теперь друзья", user, friend);
    }

    public List<User> getAllUsersFriends(Integer id) {
        User user = getUserById(id);
        return users.getUsersByIds(user.getFriendsIds());
    }

    public List<User> getCommonFriend(Integer id, Integer otherId) {
        List<User> userFriends = getAllUsersFriends(id);
        List<User> otherUserFriends = getAllUsersFriends(otherId);

        List<User> commonFriends = userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());

        log.info("Возвращены общие друзья пользователя с id={} и пользователя с id={} :{}", id, otherId, commonFriends);


        return commonFriends;
    }

    public User getUserById(Integer id) {
        User user = users.getUserById(id);
        if (user == null) {
            log.error("пользователь с запрошенным id {} не найден", id);
            throw new UserNotFoundException(String.format(
                    "пользователь с запрошенным id = %s не найден", id));
        }
        return user;
    }

    public User deleteUsersFriend(Integer id, Integer exFriendId) {
        User user = getUserById(id);
        User exFriend = getUserById(exFriendId);

        user.delFriend(exFriendId);
        log.info("User {} и User {} больше не друзья", user, exFriend);
        return user;
    }

    private void checkName(@NotNull User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("Поле name пустое, в качестве имени установлен login=\"{}\"", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private int getNewId() {
        int newId = ++ids;

        log.trace("создан новый userId id={}", newId);
        return newId;
    }
}
