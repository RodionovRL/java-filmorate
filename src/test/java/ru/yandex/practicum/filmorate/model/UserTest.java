package ru.yandex.practicum.filmorate.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private Validator validator;
    private User validUser;
    private User invalidUser;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        validUser = User.builder()
                .id(1)
                .email("validUser@ya.ru")
                .name("validUser")
                .login("validUserLogin")
                .birthday(LocalDate.of(2000, 1, 2))
                .build();

        invalidUser = new User();
    }

    @Test
    public void couldEmptyViolationsWhenValidUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);

        assertTrue(violations.isEmpty(), "Обнаружены ошибки: " + violations);
    }

    @Test
    public void couldRightErrorMessagesWhenLoginIsBlankEmailIsNotEmailBirthdayIsNull() {
        invalidUser = validUser;
        invalidUser.setLogin(null);
        invalidUser.setEmail("invalidEmail");
        invalidUser.setBirthday(null);

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        String[] expectedMessages = new String[] {"Необходимо ввести email адрес",
                "Логин не должен быть пустым",
                "Необходимо ввести логин",
                "Необходимо задать дату рождения"};

        String[] actualMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toArray(String[]::new);

        Arrays.sort(expectedMessages);
        Arrays.sort(actualMessages);

        assertFalse(violations.isEmpty(), "Ошибки не обнаружены");

        assertArrayEquals(expectedMessages, actualMessages);
    }

    @Test
    public void couldRightErrorMessagesWhenNotValidFieldsLoginIsBlankBirthdayIsFuture() {
        invalidUser = validUser;
        invalidUser.setLogin("");
        invalidUser.setBirthday(LocalDate.of(2025,1,1));

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        String[] expectedMessages =  new String[] {"Логин не должен быть пустым",
                "Пользователь ещё не родился?"};

        String[] actualMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toArray(String[]::new);

        Arrays.sort(expectedMessages);
        Arrays.sort(actualMessages);

        assertFalse(violations.isEmpty(), "Ошибки не обнаружены");

        assertArrayEquals(expectedMessages, actualMessages);
    }
}