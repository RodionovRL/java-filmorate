package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Genre {
    private int id;
    private String name;
}