package com.hiketrackbackend.hiketrackbackend.service.files;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    List<String> uploadFile(String folderName, List<MultipartFile> multipartFiles);
}