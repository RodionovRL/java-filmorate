package ru.yandex.practicum.filmorate.api;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    User addUser(User newUser);

    User updateUser(long id, User user);

    Collection<User> getAllUsers();

    User getUserById(Long id);

    List<User> getUsersFriends(Long id);

    boolean addFriend(Long id, Long friendId);

    boolean deleteFriend(Long id, Long exFriendId);

    List<Long> getUserRecommendations(long id);

    boolean deleteUserById(Long id);
}
