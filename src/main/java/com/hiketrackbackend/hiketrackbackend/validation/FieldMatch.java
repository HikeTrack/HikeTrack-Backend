package com.hiketrackbackend.hiketrackbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FieldMatchValidation.class)
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMatch {
    String message() default "Passwords should match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String password();
    String repeatPassword();
}
