package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

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
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
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
}
