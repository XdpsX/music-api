package com.xdpsx.music.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SortFieldValidator.class)
@Documented
public @interface SortFieldConstraint {
    String message() default "Invalid sort field";
    String[] sortFields();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
