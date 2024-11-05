package com.hiketrackbackend.hiketrackbackend.service.files;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    List<String> uploadFileToS3(String folderName, List<MultipartFile> multipartFiles);

    void deleteFileFromS3(String url);
}
