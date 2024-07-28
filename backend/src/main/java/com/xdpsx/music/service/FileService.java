package com.xdpsx.music.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadFile(MultipartFile file, String folder);
    void deleteFileByUrl(String fileUrl);
}
