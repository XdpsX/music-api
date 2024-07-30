package com.xdpsx.music.service;

import com.xdpsx.music.dto.request.AlbumRequest;
import com.xdpsx.music.dto.response.AlbumResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AlbumService {
    AlbumResponse createAlbum(AlbumRequest request, MultipartFile image);
    AlbumResponse updateAlbum(Long id, AlbumRequest request, MultipartFile image);
    AlbumResponse getAlbumById(Long id);
    List<AlbumResponse> getAllAlbums();
    void deleteAlbum(Long id);
}
