package com.hiketrackbackend.hiketrackbackend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public class ImageFileListValidator implements ConstraintValidator<ValidImageFile, MultipartFile> {

    @Override
    public void initialize(ValidImageFile constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // Skip empty file, so we can do work with other changes
        if (file == null || file.isEmpty()) {
            return true;
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must not exceed 5MB");
        }
        if (!Objects.equals(file.getContentType(), "image/jpeg")
                && !Objects.equals(file.getContentType(), "image/png")
                && !Objects.equals(file.getContentType(), "image/jpg")) {
            throw new IllegalArgumentException("Only JPEG, PNG, JPG files are allowed");
        }
        return true;
    }
}
