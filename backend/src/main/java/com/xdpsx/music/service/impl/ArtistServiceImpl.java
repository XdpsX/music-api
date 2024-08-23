package com.xdpsx.music.service.impl;

import com.xdpsx.music.constant.Keys;
import com.xdpsx.music.dto.request.params.ArtistParams;
import com.xdpsx.music.dto.request.ArtistRequest;
import com.xdpsx.music.dto.response.ArtistResponse;
import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.mapper.PageMapper;
import com.xdpsx.music.model.entity.Artist;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.ArtistMapper;
import com.xdpsx.music.repository.ArtistRepository;
import com.xdpsx.music.service.ArtistService;
import com.xdpsx.music.service.FileService;
import com.xdpsx.music.util.I18nUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.xdpsx.music.constant.FileConstants.ARTISTS_IMG_FOLDER;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    private final FileService fileService;
    private final ArtistMapper artistMapper;
    private final PageMapper pageMapper;
    private final ArtistRepository artistRepository;
    private final I18nUtils i18nUtils;

    @Override
    @Cacheable(cacheNames = Keys.ARTISTS, key = "#params")
    public PageResponse<ArtistResponse> getAllArtists(ArtistParams params) {
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Artist> artistPage = artistRepository.findWithFilters(
                pageable, params.getSearch(), params.getSort(), params.getGender()
        );
        return pageMapper.toArtistPageResponse(artistPage);
    }

    @Override
    @CachePut(value = Keys.ARTIST_ITEM, key = "#result.id")
    @Caching(evict = {
            @CacheEvict(value = Keys.ARTISTS, allEntries = true)
    })
    @Transactional
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
    @CachePut(value = Keys.ARTIST_ITEM, key = "#result.id")
    @Caching(evict = {
            @CacheEvict(value = Keys.ARTISTS, allEntries = true)
    })
    @Transactional
    public ArtistResponse updateArtist(Long id, ArtistRequest request, MultipartFile image) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getArtistNotFoundMsg(id)));
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
    @Cacheable(value = Keys.ARTIST_ITEM, key = "#id")
    public ArtistResponse getArtistById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getArtistNotFoundMsg(id)));
        return artistMapper.fromEntityToResponse(artist);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = Keys.ARTIST_ITEM, key = "#id"),
            @CacheEvict(value = Keys.ARTISTS, allEntries = true)
    })
    @Transactional
    public void deleteArtist(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getArtistNotFoundMsg(id)));
        artistRepository.delete(artist);
        fileService.deleteFileByUrl(artist.getAvatar());
    }
}
