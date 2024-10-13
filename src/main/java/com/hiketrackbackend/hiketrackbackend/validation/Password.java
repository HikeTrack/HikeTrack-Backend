package com.hiketrackbackend.hiketrackbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidation.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Password should contains letters (uppercase and lowercase), "
            + "digits, and specified special characters are allowed. "
            + "The minimum password length is 8 characters.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
