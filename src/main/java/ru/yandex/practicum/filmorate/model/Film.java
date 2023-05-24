package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.util.AfterInternationCinemaDay;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private int id;
    @NotNull(message = "Не задано название фильма")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Описание должно быть не более 200 символов")
    private String description;
    @AfterInternationCinemaDay(message = "Дата релиза не может быть ранее 28 декабря 1895!")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    private final Set<Integer> likes = new HashSet<>();

    public int compareByLikes(Film f2) {
        return f2.getLikes().size() - likes.size();
    }

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void delLike(int userId) {
        likes.remove(userId);
    }
}
