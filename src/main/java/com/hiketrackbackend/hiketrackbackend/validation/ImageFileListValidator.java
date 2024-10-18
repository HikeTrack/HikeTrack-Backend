package com.hiketrackbackend.hiketrackbackend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImageFileListValidator implements ConstraintValidator<ValidImageFileList, List<MultipartFile>> {

    @Override
    public void initialize(ValidImageFileList constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        // Skip empty list, so we can do work with other changes
        if (files == null || files.isEmpty()) {
            return true;
        }

        for (MultipartFile file : files) {
            // Skip empty file, so we can do work with other changes
            if (file == null) {
                continue;
            }

            String contentType = file.getContentType();
            if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
                return false;
            }
        }
        return true;
    }
}
