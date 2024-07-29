package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.request.ArtistParams;
import com.xdpsx.music.dto.request.ArtistRequest;
import com.xdpsx.music.dto.response.ArtistResponse;
import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.entity.Artist;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.ArtistMapper;
import com.xdpsx.music.repository.ArtistRepository;
import com.xdpsx.music.service.ArtistService;
import com.xdpsx.music.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.xdpsx.music.constant.FileContants.ARTISTS_IMG_FOLDER;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    private final FileService fileService;
    private final ArtistMapper artistMapper;
    private final ArtistRepository artistRepository;

    @Override
    public PageResponse<ArtistResponse> getAllArtists(ArtistParams params) {
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Artist> artistPage = artistRepository.findWithFilters(
                pageable, params.getSearch(), params.getSort(), params.getGender()
        );
        List<ArtistResponse> responses = artistPage.getContent().stream()
                .map(artistMapper::fromEntityToResponse)
                .collect(Collectors.toList());
        return PageResponse.<ArtistResponse>builder()
                .items(responses)
                .pageNum(artistPage.getNumber() + 1)
                .pageSize(artistPage.getSize())
                .totalItems(artistPage.getTotalElements())
                .totalPages(artistPage  .getTotalPages())
                .build();
    }

    @Override
    public ArtistResponse createArtist(ArtistRequest request, MultipartFile image) {
        Artist artist = artistMapper.fromRequestToEntity(request);
        if (image != null){
            String imageUrl = fileService.uploadFile(image, ARTISTS_IMG_FOLDER);
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
            String imageUrl = fileService.uploadFile(image, ARTISTS_IMG_FOLDER);
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
