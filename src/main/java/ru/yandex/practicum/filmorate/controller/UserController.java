package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

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

    @PutMapping("")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("получен запрос на обновление пользователя {}", user);
        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Collection<User>> getAllUsers() {
        log.info("получен запрос на получение всех пользователей");
        Collection<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable(value = "id") int id
    ) {
        log.info("получен запрос на получение пользователя по id {}", id);

        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<List<Integer>> addNewFriend(@PathVariable("id") Integer id,
                                                      @PathVariable("friendId") Integer friendId
    ) {
        log.info("получен запрос на добавление пользователю с id {} друга с id {}", id, friendId);

        userService.addFriend(id, friendId);
        return new ResponseEntity<>(Arrays.asList(id, friendId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable("id") Integer id,
                                             @PathVariable("friendId") Integer friendId
    ) {
        log.info("получен запрос на удаление друга с id {} у пользователя пользователю с id {} ", friendId, id);

        User user = userService.deleteUsersFriend(id, friendId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<User>> getUsersFriends(@PathVariable("id") Integer id) {
        log.info("получен запрос всех друзей пользователя с id {}", id);

        Collection<User> friends = userService.getAllUsersFriends(id);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getCommonFriends(@PathVariable("id") Integer id,
                                                             @PathVariable("otherId") Integer otherId
    ) {
        log.info("получен запрос общих друзей для {} и {}", id, otherId);

        List<User> commonFriends = userService.getCommonFriend(id, otherId);
        return new ResponseEntity<>(commonFriends, HttpStatus.OK);
    }


}
