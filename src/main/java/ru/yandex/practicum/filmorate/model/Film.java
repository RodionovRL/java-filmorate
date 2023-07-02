package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.util.AfterInternationCinemaDay;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

import static java.util.Comparator.comparing;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    @EqualsAndHashCode.Exclude
    private long id;
    @NotNull(message = "Не задано название фильма")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Описание должно быть не более 200 символов")
    private String description;
    @AfterInternationCinemaDay(message = "Дата релиза не может быть ранее 28 декабря 1895!")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    private Set<Director> directors;

    private Mpa mpa;

    private Set<Genre> genres = new TreeSet<>(comparing(Genre::getId, Comparator.naturalOrder()));

//    private Map<Long, Integer> marks = new HashMap();

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa_id", mpa.getId());
        values.put("directors", directors);

        return values;
    }
}
