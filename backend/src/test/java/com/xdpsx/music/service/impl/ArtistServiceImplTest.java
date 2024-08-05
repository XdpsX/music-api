package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.params.ArtistParams;
import com.xdpsx.music.dto.request.ArtistRequest;
import com.xdpsx.music.dto.response.ArtistResponse;
import com.xdpsx.music.model.entity.Artist;
import com.xdpsx.music.model.enums.Gender;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.ArtistMapper;
import com.xdpsx.music.repository.ArtistRepository;
import com.xdpsx.music.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceImplTest {
    @Mock
    private FileService fileService;

    @Mock
    private ArtistMapper artistMapper;

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistServiceImpl artistService;

    private ArtistRequest artistRequest;
    private Artist artist;
    private ArtistResponse artistResponse;
    private MultipartFile imageFile;
    private ArtistParams artistParams;

    @BeforeEach
    void setUp() {
        artistRequest = ArtistRequest.builder()
                .name("John Doe")
                .gender(Gender.MALE)
                .description("A popular artist")
                .dob(LocalDate.of(1990, 1, 1))
                .build();

        artist = Artist.builder()
                .id(1L)
                .name("John Doe")
                .gender(Gender.MALE)
                .description("A popular artist")
                .dob(LocalDate.of(1990, 1, 1))
                .avatar("avatar.jpg")
                .build();

        artistResponse = ArtistResponse.builder()
                .id(1L)
                .name("John Doe")
                .gender(Gender.MALE)
                .description("A popular artist")
                .dob(LocalDate.of(1990, 1, 1))
                .avatar("avatar.jpg")
                .build();


        imageFile = mock(MultipartFile.class);

        artistParams = new ArtistParams();
        artistParams.setPageNum(1);
        artistParams.setPageSize(10);
        artistParams.setSearch("John");
        artistParams.setSort("name");
        artistParams.setGender(Gender.MALE);
    }

    @Test
    void getAllArtists_whenProvideParams_returnFilteredArtists() {
        Pageable pageable = PageRequest.of(artistParams.getPageNum() - 1, artistParams.getPageSize());
        Page<Artist> artistPage = new PageImpl<>(Collections.singletonList(artist));
        when(artistRepository.findWithFilters(pageable, artistParams.getSearch(), artistParams.getSort(), artistParams.getGender()))
                .thenReturn(artistPage);
        when(artistMapper.fromEntityToResponse(any(Artist.class))).thenReturn(artistResponse);

        PageResponse<ArtistResponse> result = artistService.getAllArtists(artistParams);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        verify(artistRepository).findWithFilters(pageable, artistParams.getSearch(), artistParams.getSort(), artistParams.getGender());
        verify(artistMapper).fromEntityToResponse(any(Artist.class));
    }

    @Test
    void createArtist_shouldCreateSuccessfully() {
        when(artistMapper.fromRequestToEntity(artistRequest)).thenReturn(artist);
        when(fileService.uploadFile(imageFile, "artists")).thenReturn("avatar.jpg");
        when(artistRepository.save(any(Artist.class))).thenReturn(artist);
        when(artistMapper.fromEntityToResponse(artist)).thenReturn(artistResponse);

        ArtistResponse result = artistService.createArtist(artistRequest, imageFile);

        assertNotNull(result);
        assertEquals(artistResponse, result);
        verify(artistMapper).fromRequestToEntity(artistRequest);
        verify(fileService).uploadFile(imageFile, "artists");
        verify(artistRepository).save(any(Artist.class));
        verify(artistMapper).fromEntityToResponse(artist);
    }

    @Test
    void updateArtist_shouldUpdateSuccessfully() {
        when(artistRepository.findById(artist.getId())).thenReturn(Optional.of(artist));
        when(fileService.uploadFile(imageFile, "artists")).thenReturn("new_avatar.jpg");
        when(artistRepository.save(any(Artist.class))).thenReturn(artist);
        when(artistMapper.fromEntityToResponse(artist)).thenReturn(artistResponse);

        ArtistResponse result = artistService.updateArtist(artist.getId(), artistRequest, imageFile);

        assertNotNull(result);
        assertEquals(artistResponse, result);
        verify(artistRepository).findById(artist.getId());
        verify(fileService).uploadFile(imageFile, "artists");
        verify(artistRepository).save(any(Artist.class));
        verify(fileService).deleteFileByUrl("avatar.jpg");
        verify(artistMapper).fromEntityToResponse(artist);
    }

    @Test
    void updateArtist_whenArtistNotFound_throwResourceNotFoundException() {
        when(artistRepository.findById(artist.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> artistService.updateArtist(artist.getId(), artistRequest, imageFile));

        verify(artistRepository).findById(artist.getId());
        verify(fileService, never()).uploadFile(any(), any());
        verify(artistRepository, never()).save(any());
        verify(fileService, never()).deleteFileByUrl(any());
        verify(artistMapper, never()).fromEntityToResponse(any());
    }

    @Test
    void getArtistById_returnArtist() {
        when(artistRepository.findById(artist.getId())).thenReturn(Optional.of(artist));
        when(artistMapper.fromEntityToResponse(artist)).thenReturn(artistResponse);

        ArtistResponse result = artistService.getArtistById(artist.getId());

        assertNotNull(result);
        assertEquals(artistResponse, result);
        verify(artistRepository).findById(artist.getId());
        verify(artistMapper).fromEntityToResponse(artist);
    }

    @Test
    void getArtistById_whenArtistNotFound_throwResourceNotFoundException() {
        when(artistRepository.findById(artist.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> artistService.getArtistById(artist.getId()));

        verify(artistRepository).findById(artist.getId());
        verify(artistMapper, never()).fromEntityToResponse(any());
    }

    @Test
    void deleteArtist_shouldDeleteSuccessfully() {
        when(artistRepository.findById(artist.getId())).thenReturn(Optional.of(artist));

        artistService.deleteArtist(artist.getId());

        verify(artistRepository).findById(artist.getId());
        verify(fileService).deleteFileByUrl(artist.getAvatar());
        verify(artistRepository).delete(artist);
    }

    @Test
    void deleteArtist_whenArtistNotFound_throwResourceNotFoundException() {
        when(artistRepository.findById(artist.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> artistService.deleteArtist(artist.getId()));

        verify(artistRepository).findById(artist.getId());
        verify(fileService, never()).deleteFileByUrl(any());
        verify(artistRepository, never()).delete(any());
    }
}