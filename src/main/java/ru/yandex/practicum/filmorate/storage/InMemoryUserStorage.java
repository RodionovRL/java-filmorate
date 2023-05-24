package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private static int ids;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User newUser) {
        int id = getNewId();
        newUser.setId(id);
        users.put(id, newUser);
        return newUser;
    }

    @Override
    public User updateUser(int id, User user) {
        checkUserIsContains(id);
        user.setId(id);
        return users.replace(id, user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Integer id) {
        checkUserIsContains(id);
        return users.get(id);
    }

    @Override
    public List<User> getUsersByIds(Set<Integer> friendsIds) {
        if (friendsIds == null) {
            return new ArrayList<>();
        }

        return users.entrySet().stream()
                .filter(x -> friendsIds.contains(x.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private void checkUserIsContains(Integer id) {
        if (!users.containsKey(id)) {
            log.error("пользователь с запрошенным id {} не найден", id);
            throw new UserNotFoundException(String.format(
                    "пользователь с запрошенным id = %s не найден", id));
        }
    }

    private int getNewId() {
        int newId = ++ids;
        log.trace("создан новый userId = {}", newId);
        return newId;
    }
}
