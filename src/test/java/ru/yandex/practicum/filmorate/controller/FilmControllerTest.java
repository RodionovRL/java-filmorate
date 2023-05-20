package ru.yandex.practicum.filmorate.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig
@WebMvcTest(FilmController.class)
@ActiveProfiles("test")
public class FilmControllerTest {

    @MockBean
    private FilmService filmService;

    @InjectMocks
    private FilmController controller;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPostFilmWithNullName() throws Exception {
        Film film = new Film(1,
                null,
                "Description",
                LocalDate.of(1895, 12, 27),
                -1,
                new HashSet<>());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> {
                    MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();
                    assert ex != null;
                    List<ObjectError> errors = ex.getBindingResult().getAllErrors();
                    assertTrue(errors.stream()
                            .anyMatch(error -> Objects.requireNonNull(error.getDefaultMessage())
                                    .contains("Не задано название фильма")));
                    assertTrue(errors.stream()
                            .anyMatch(error -> Objects.requireNonNull(error.getDefaultMessage())
                                    .contains("Дата релиза не может быть ранее 28 декабря 1895!")));
                    assertTrue(errors.stream()
                            .anyMatch(error -> Objects.requireNonNull(error.getDefaultMessage())
                                    .contains("Продолжительность фильма должна быть положительной")));
                });
    }

    @Test
    void testPostFilmWithBlankName() throws Exception {
        Film film = new Film(1,
                "",
                "Description",
                LocalDate.now(),
                120,
                new HashSet<>());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> {
                    MethodArgumentNotValidException ex = (MethodArgumentNotValidException) result.getResolvedException();
                    assert ex != null;
                    List<ObjectError> errors = ex.getBindingResult().getAllErrors();
                    assertTrue(errors.stream()
                            .anyMatch(error -> Objects.requireNonNull(error.getDefaultMessage())
                                    .contains("Название фильма не может быть пустым")));
                });
    }


}