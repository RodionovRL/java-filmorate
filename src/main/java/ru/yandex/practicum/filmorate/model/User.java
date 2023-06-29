package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private long id;
    @Email(message = "Необходимо ввести email адрес")
    private String email;
    @NotNull(message = "Необходимо ввести логин")
    @NotBlank(message = "Логин не должен быть пустым")
    private String login;
    private String name;
    @NotNull(message = "Необходимо задать дату рождения")
    @Past(message = "Пользователь ещё не родился?")
    private Date birthday;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("email", email);
        values.put("login", login);
        values.put("birthday", birthday);
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getLogin(), user.getLogin()) && Objects.equals(getName(), user.getName()) && Objects.equals(getBirthday(), user.getBirthday());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getLogin(), getName(), getBirthday());
    }
}
