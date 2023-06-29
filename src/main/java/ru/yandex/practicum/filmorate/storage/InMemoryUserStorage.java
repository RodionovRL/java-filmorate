package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.api.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private static long ids;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User newUser) {
        long id = getNewId();
        newUser.setId(id);
        users.put(id, newUser);
        return newUser;
    }

    @Override
    public User updateUser(long id, User user) {
        checkUserIsExist(id);
        user.setId(id);
        return users.replace(id, user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        checkUserIsExist(id);
        return users.get(id);
    }

    @Override
    public boolean deleteUserById(Long id) {
        getUsersFriends(id).forEach(f -> f.delFriend(id));
        return users.remove(id) != null;
    }

    @Override
    public List<User> getUsersFriends(Long id) {
        User user = getUserById(id);
        if (user.getFriendsIds() == null) {
            return new ArrayList<>();
        }

        return users.entrySet().stream()
                .filter(x -> user.getFriendsIds().contains(x.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public boolean addFriend(Long id, Long friendId) {
        User user = getUserById(id);
        checkUserIsExist(friendId);

        return user.addFriend(friendId);
    }

    @Override
    public boolean deleteFriend(Long id, Long exFriendId) {
        User user = getUserById(id);
        checkUserIsExist(exFriendId);

        return user.delFriend(exFriendId);
    }

    @Override
    public Set<Long> getUserRecommendations(long id) {
        return new HashSet<>();
    }

    private void checkUserIsExist(Long id) {
        if (!users.containsKey(id)) {
            log.error("пользователь с запрошенным id {} не найден", id);
            throw new NotFoundException(String.format(
                    "пользователь с запрошенным id = %s не найден", id));
        }
    }

    private long getNewId() {
        long newId = ++ids;
        log.trace("создан новый userId = {}", newId);
        return newId;
    }
}
