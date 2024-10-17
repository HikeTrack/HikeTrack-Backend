package com.hiketrackbackend.hiketrackbackend.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ImageFileListValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageFileList {
    String message() default "Список файлів містить непідтримуваний тип файлу. Допустимі тільки JPEG або PNG.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
