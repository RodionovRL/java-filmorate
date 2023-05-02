package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserServiceException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService {

    private static int ids;
    private final Map<Integer, User> users = new HashMap<>();

    public User addUser(@NotNull User newUser) {
        int id = getNewId();

        newUser.setId(id);
        checkName(newUser);
        users.put(id, newUser);
        log.info("Добавлен пользователь {}", newUser);
        return newUser;
    }

    public User updateUser(@NotNull User user) {

        if (!users.containsKey(user.getId())) {
            log.error("updateUser: пользователь с id={} не найден", user.getId());
            throw new UserServiceException("updateUser: пользователь с запрошенным id не найден");
        }
        checkName(user);
        User oldUser = users.replace(user.getId(), user);
        log.info("Информация о пользователе {} изменена на {}", oldUser, user);
        return user;
    }

    public Collection<User> getAllUsers() {

        log.info("переданы все {} пользователей", users.values().size());
        return users.values();
    }

    private int getNewId() {
        int newId = ++ids;

        log.trace("создан новый userId id={}", newId);
        return newId;
    }

    private void checkName(@NotNull User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("Поле name пустое, в качестве имени установлен login=\"{}\"", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
