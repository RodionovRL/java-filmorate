package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserServiceException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    UserService userService;
    User user;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        user = new User(Integer.MAX_VALUE,
                "TestUser@ya.ru",
                "TestUserLogin",
                "",
                LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldBeUserServiceExceptionWhenUpdateUserWithUnknownId() {
        UserServiceException exception = Assertions.assertThrows(UserServiceException.class,
                () -> userService.updateUser(user));

        assertEquals("updateUser: пользователь с запрошенным id не найден", exception.getMessage());
    }

    @Test
    void shouldSetNameSameLoginWhenNameIsEmpty() {
        userService.addUser(user);

        assertEquals(user.getName(), user.getLogin());
    }
}