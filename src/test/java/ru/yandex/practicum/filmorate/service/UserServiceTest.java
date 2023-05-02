package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserServiceException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    UserService userService;
    User user;

    @BeforeEach
    void setUp() {
        userService = new UserService();

        user = User.builder()
                .id(Integer.MAX_VALUE)
                .email("TestUser@ya.ru")
                .login("TestUserLogin")
                .name("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    void shouldBeUserServiceExceptionWhenUpdateUserWithUnknownId() {
        UserServiceException exception = Assertions.assertThrows(UserServiceException.class,
                () -> userService.updateUser(user));

        assertEquals("updateUser: пользователь с запрошенным id не найден", exception.getMessage());
    }

    @Test
    void shouldSetNameSameLoginWhenAddUserAndNameIsEmpty() {
        userService.addUser(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void shouldSetNameSameLoginWhenUpdateUserAndNameIsEmpty() {
        user.setName("TestName");
        int updateId = userService.addUser(user).getId();
        user.setName("");
        user.setId(updateId);
        userService.updateUser(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void testUpdateUserAndAllUserFields() {

        String updateEmail = "updatedMail@ya.ru";
        String updateLogin = "UpdatedLogin";
        String updateName = "UpdatedName";
        LocalDate updateBirthday = LocalDate.of(2002, 2, 2);

        int updateId = userService.addUser(user).getId();

        User updateUser = User.builder()
                .id(updateId)
                .email(updateEmail)
                .login(updateLogin)
                .name(updateName)
                .birthday(updateBirthday)
                .build();

        User updatedUser = userService.updateUser(updateUser);

        assertAll("User fields",
                () -> assertEquals(updatedUser.getEmail(), updatedUser.getEmail(), "email не совпадает"),
                () -> assertEquals(updatedUser.getLogin(), updatedUser.getLogin(), "login не совпадает"),
                () -> assertEquals(updatedUser.getName(), updatedUser.getName(), "name не совпадает"),
                () -> assertEquals(updatedUser.getBirthday(), updatedUser.getBirthday(),
                        "birthday не совпадает"),
                () -> assertEquals(updatedUser, updatedUser, "метод вернул другой объект")
        );

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void testGetAllUsers() {
        User user1 = User.builder()
                .id(0)
                .email("mail1.ya.ru")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1981, 1, 1))
                .build();
        User user2 = User.builder()
                .id(1)
                .email("mail2.ya.ru")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(1981, 1, 2))
                .build();
        User user3 = User.builder()
                .id(2)
                .email("mail3.ya.ru")
                .login("login3")
                .name("name3")
                .birthday(LocalDate.of(1981, 1, 3))
                .build();
        User user4 = User.builder()
                .id(3)
                .email("mail4.ya.ru")
                .login("login4")
                .name("name4")
                .birthday(LocalDate.of(1981, 1, 4))
                .build();
        User user5 = User.builder()
                .id(4)
                .email("mail5.ya.ru")
                .login("login5")
                .name("name5")
                .birthday(LocalDate.of(1981, 1, 5))
                .build();

        List<User> testUsers = new ArrayList<>();

        testUsers.add(user);
        testUsers.add(user1);
        testUsers.add(user2);
        testUsers.add(user3);
        testUsers.add(user4);
        testUsers.add(user5);

        userService.addUser(user);
        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
        userService.addUser(user4);
        userService.addUser(user5);

        assertArrayEquals(testUsers.toArray(), userService.getAllUsers().toArray());
    }
}