package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.request.ArtistRequest;
import com.xdpsx.music.dto.response.ArtistResponse;
import com.xdpsx.music.entity.Artist;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.ArtistMapper;
import com.xdpsx.music.repository.ArtistRepository;
import com.xdpsx.music.service.ArtistService;
import com.xdpsx.music.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    private final FileService fileService;
    private final ArtistMapper artistMapper;
    private final ArtistRepository artistRepository;

    private final static String IMG_FOLDER = "artists";

    @Override
    public List<ArtistResponse> getAllArtists() {
        List<Artist> artists = artistRepository.findAll();
        return artists.stream()
                .map(artistMapper::fromEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ArtistResponse createArtist(ArtistRequest request, MultipartFile image) {
        Artist artist = artistMapper.fromRequestToEntity(request);
        if (image != null){
            String imageUrl = fileService.uploadFile(image, IMG_FOLDER);
            artist.setAvatar(imageUrl);
        }

        Artist savedArtist = artistRepository.save(artist);
        return artistMapper.fromEntityToResponse(savedArtist);
    }

    @Override
    public ArtistResponse updateArtist(Long id, ArtistRequest request, MultipartFile image) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found artist with ID=%s", id)));
        artist.setName(request.getName());
        artist.setGender(request.getGender());
        artist.setDescription(request.getDescription());
        artist.setDob(request.getDob());

        String oldImage = null;
        if (image != null){
            oldImage = artist.getAvatar();
            String imageUrl = fileService.uploadFile(image, IMG_FOLDER);
            artist.setAvatar(imageUrl);
        }

        Artist savedArtist = artistRepository.save(artist);
        if (oldImage != null){
            fileService.deleteFileByUrl(oldImage);
        }
        return artistMapper.fromEntityToResponse(savedArtist);
    }

    @Override
    public ArtistResponse getArtistById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found artist with ID=%s", id)));
        return artistMapper.fromEntityToResponse(artist);
    }

    @Override
    public void deleteArtist(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found artist with ID=%s", id)));
        fileService.deleteFileByUrl(artist.getAvatar());
        artistRepository.delete(artist);
    }
}
