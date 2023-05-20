package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void put(int id, User newUser) {
        users.put(id, newUser);
    }

    @Override
    public User replace(int id, User user) {
        return users.replace(id, user);
    }

    @Override
    public Collection<User> values() {
        return users.values();
    }

    @Override
    public User getUserById(Integer id) {

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


}
