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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
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

    private Set<Integer> friendsIds = new HashSet<>();

    public void addFriend(Integer id) {
        friendsIds.add(id);
        log.info("добавлен друг с id {}", id);
    }

    public void delFriend(Integer id) {
        friendsIds.remove(id);

        log.info("Удалён друг с id {}", id);
    }
}
