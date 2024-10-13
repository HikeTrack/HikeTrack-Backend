package com.hiketrackbackend.hiketrackbackend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import java.util.Objects;

public class FieldMatchValidation implements ConstraintValidator<FieldMatch, Object> {
    private String password;
    private String repeatPassword;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        password = constraintAnnotation.password();
        repeatPassword = constraintAnnotation.repeatPassword();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        Object field = new BeanWrapperImpl(value).getPropertyValue(this.password);
        Object fieldMatch = new BeanWrapperImpl(value).getPropertyValue(this.repeatPassword);
        return Objects.equals(field, fieldMatch);
    }
}
