package com.hiketrackbackend.hiketrackbackend.service.files;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    List<String> uploadFileToS3(String folderName, List<MultipartFile> multipartFiles);

    void deleteFileFromS3(String keyName);
}