package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.util.stream.Stream;

@Slf4j
public class SearchParamToEnumConverter implements Converter<String, SearchBy> {
    @Override
    public SearchBy convert(String value) {
        log.debug("конвертация параметра запроса {} в enum", value);
        return Stream.of(SearchBy.values())
                .filter(s -> s.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
