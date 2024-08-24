package com.linh.identity_service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/// class xu ly
public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {
    private int min;
    // ham xu ly

    // ham chay dau tien, lay duoc thong so cua annotation do,
    // vd: luc nay nhap min = 18, thi no se lay 18 va gan lai vao min, khi biet min = 18 moi dem so sanh o isValid()
    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) return true;

        Long years = ChronoUnit.YEARS.between(localDate, LocalDate.now());
        return years >= min;
    }


}
