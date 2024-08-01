package com.xdpsx.music.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SortFieldValidator implements ConstraintValidator<SortFieldConstraint, String> {
    private Set<String> validFields;

    @Override
    public void initialize(SortFieldConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        validFields = new HashSet<>(Arrays.asList(constraintAnnotation.sortFields()));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String actualField = value;
        if (value.startsWith("-")){
            actualField = value.substring(1);
        }

        if (!validFields.contains(actualField)) {
            String message = validFields.stream().collect(Collectors.joining(", "));
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid sort field. Accept: " + message).addConstraintViolation();
            return false;
        }
        return true;
    }
}
