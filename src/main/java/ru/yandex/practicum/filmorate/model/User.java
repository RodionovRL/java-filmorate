package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
@Builder
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
    @NotNull(message = "Необходимо задать дату рождения")
    @Past(message = "Пользователь ещё не родился?")
    private LocalDate birthday;
}
