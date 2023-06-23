package ru.yandex.practicum.filmorate.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchBy {
    TITLE("title"),
    DIRECTOR("director"),
    TITLE_DIRECTOR("title,director"),
    DIRECTOR_TITLE("director,title");

    private final String value;

}
