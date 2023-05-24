package ru.yandex.practicum.filmorate.api;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserStorage {

    User addUser(User newUser);

    User updateUser(int id, User user);

    Collection<User> getAllUsers();

    User getUserById(Integer id);

    List<User> getUsersByIds(Set<Integer> friendsIds);

}
