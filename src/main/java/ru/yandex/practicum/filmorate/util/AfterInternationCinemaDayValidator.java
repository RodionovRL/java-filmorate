package ru.yandex.practicum.filmorate.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AfterInternationCinemaDayValidator implements ConstraintValidator<AfterInternationCinemaDay, LocalDate> {

    final private LocalDate INTERNATION_CINEMA_DAY = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(AfterInternationCinemaDay constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(INTERNATION_CINEMA_DAY);
    }
}
