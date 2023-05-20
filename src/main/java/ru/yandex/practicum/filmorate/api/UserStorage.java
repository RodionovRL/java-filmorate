package ru.yandex.practicum.filmorate.api;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserStorage {

    void put(int id, User newUser);

    User replace(int id, User user);

    Collection<User> values();

    User getUserById(Integer id);

    List<User> getUsersByIds(Set<Integer> friendsIds);

}
