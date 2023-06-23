package ru.yandex.practicum.filmorate.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class SearchRequestParamValidator implements ConstraintValidator<SearchRequestParam, List<String>> {
    @Override
    public void initialize(SearchRequestParam constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<String> by, ConstraintValidatorContext constraintValidatorContext) {
//        if (by.size() == 1 && (by.get(0).equals("title") || by.get(0).equals("director")))
        return false;
    }
}
