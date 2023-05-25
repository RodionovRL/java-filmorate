package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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

    public void addFriend(Integer id, Integer friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        user.addFriend(friendId);
        friend.addFriend(id);

        log.info("User {} и User {} теперь друзья", user, friend);
    }

    public List<User> getAllUsersFriends(Integer id) {
        User user = getUserById(id);
        return userStorage.getUsersByIds(user.getFriendsIds());
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

        return userStorage.getUserById(id);
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

}
