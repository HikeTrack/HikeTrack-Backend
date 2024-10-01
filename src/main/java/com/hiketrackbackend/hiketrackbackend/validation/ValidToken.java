package com.hiketrackbackend.hiketrackbackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TokenValidation.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidToken {
    String message() default "Invalid or expired token.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
