package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;
    @Email(message = "Необходимо ввести email адрес")
    private String email;
    @NotNull(message = "Необходимо ввести логин")
    @NotBlank(message = "Логин не должен быть пустым")
    private String login;
    private String name;
    @Past(message = "Пользователь ещё не родился?")
    private LocalDate birthday;
}
