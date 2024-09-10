package com.xdpsx.music.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.xdpsx.music.exception.BadRequestException;
import com.xdpsx.music.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileService implements FileService {
    @Value("${aws.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = folder + "/" + UUID.randomUUID();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
            return s3Client.getUrl(bucketName, fileName).toString();
        }catch (IOException e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void deleteFileByUrl(String fileUrl) {
        if (fileUrl != null && !fileUrl.isBlank()){
            String fileName = getFileNameFromUrl(fileUrl);
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        }
    }

    private String getFileNameFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            return url.getPath().substring(1);
        } catch (Exception e) {
            throw new BadRequestException("Invalid URL: " + fileUrl);
        }
    }
}
