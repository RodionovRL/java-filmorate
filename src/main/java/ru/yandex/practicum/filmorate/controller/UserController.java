package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> postUser(@Valid @RequestBody User user) {
        log.info("получен запрос на добавление пользователя");
        User savedUser = userService.addUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("получен запрос на обновление пользователя {}", user);
        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getAllUsers() {
        log.info("получен запрос на получение всех пользователей");
        Collection<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable(value = "id") long id
    ) {
        log.info("получен запрос на получение пользователя по id {}", id);

        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Boolean> addNewFriend(@PathVariable("id") Long id,
                                                @PathVariable("friendId") Long friendId
    ) {
        log.info("получен запрос на добавление пользователю id={} друга id={}", id, friendId);

        boolean result = userService.addFriend(id, friendId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Boolean> deleteFriend(@PathVariable("id") Long id,
                                                @PathVariable("friendId") Long friendId
    ) {
        log.info("получен запрос на разрыв дружбы пользователей с id {} и id {}", friendId, id);

        boolean result = userService.deleteUsersFriend(id, friendId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<User>> getUsersFriends(@PathVariable("id") Long id) {
        log.info("получен запрос всех друзей пользователя с id {}", id);

        Collection<User> friends = userService.getAllUsersFriends(id);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }


    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getCommonFriends(@PathVariable("id") Long id,
                                                             @PathVariable("otherId") Long otherId
    ) {
        log.info("получен запрос общих друзей для {} и {}", id, otherId);

        List<User> commonFriends = userService.getCommonFriend(id, otherId);
        return new ResponseEntity<>(commonFriends, HttpStatus.OK);
    }

    @GetMapping("/{id}/recommendations")
    public ResponseEntity<Collection<Film>> getUserRecommendations(@PathVariable("id") Long id) {
        log.info("получен запрос рекомендаций для пользователя с id {}", id);
        Collection<Film> films = userService.getUserRecommendations(id);
        log.info("возвращены рекомендации для пользователя с id {}", id);
        return new ResponseEntity<>(films, HttpStatus.OK);
    }
}
