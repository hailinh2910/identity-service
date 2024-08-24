package com.linh.identity_service.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {DobValidator.class}
)
//custom validation, ex: 18+ age
public @interface DobConstraint {
    String message();

    int min();


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
