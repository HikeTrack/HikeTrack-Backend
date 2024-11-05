package com.hiketrackbackend.hiketrackbackend.service.files;

import com.hiketrackbackend.hiketrackbackend.exception.FileUploadException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class S3Service implements FileStorageService {
    private final S3Client s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Override
    public List<String> uploadFileToS3(String folderName, List<MultipartFile> multipartFiles) {
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
                throw new FileUploadException("BaseFile can`t be downloaded: "
                        + multipartFile + ". " + e.getMessage());
            } finally {

                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }
        return fileUrls;
    }

    @Override
    public void deleteFileFromS3(String url) {
        int lastSlashIndex = url.lastIndexOf("/");
        String keyName = url.substring(lastSlashIndex + 1);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }
}
