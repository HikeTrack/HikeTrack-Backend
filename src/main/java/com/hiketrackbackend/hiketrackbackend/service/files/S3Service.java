package com.hiketrackbackend.hiketrackbackend.service.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service implements FileStorageService {
    private final S3Client s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Override
    public List<String> uploadFile(String folderName, List<MultipartFile> multipartFiles) {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String keyName = folderName + "/" + multipartFile.getOriginalFilename();
            File tempFile = null;
            try {
                tempFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
                multipartFile.transferTo(tempFile);

                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build();

                s3Client.putObject(request, tempFile.toPath());

                String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + keyName;
                fileUrls.add(fileUrl);
            } catch (IOException e) {
                throw new RuntimeException("File can`t be downloaded: " + multipartFile + ". " + e.getMessage(), e);
            } finally {

                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }
        return fileUrls;
    }
}
