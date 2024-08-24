package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.request.ArtistRequest;
import com.xdpsx.music.dto.response.ArtistResponse;
import com.xdpsx.music.model.entity.Artist;
import com.xdpsx.music.model.enums.Gender;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.ArtistMapper;
import com.xdpsx.music.repository.ArtistRepository;
import com.xdpsx.music.service.FileService;
import com.xdpsx.music.util.I18nUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.xdpsx.music.constant.FileConstants.ARTISTS_IMG_FOLDER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceImplTest {
    @Mock private ArtistRepository artistRepository;
    @Mock private ArtistMapper artistMapper;
    @Mock private FileService fileService;
    @Mock private I18nUtils i18nUtils;

    @InjectMocks private ArtistServiceImpl artistService;

    @DisplayName(value = "Create artist with image successfully")
    @Test
    public void testCreateArtist_ShouldSaveArtist_WhenImageProvided() {
        // Arrange
        ArtistRequest request = new ArtistRequest();
        request.setName("Test Artist");
        request.setGender(Gender.MALE);

        MultipartFile image = mock(MultipartFile.class);
        String imageUrl = "artist.jpg";

        Artist artist = new Artist();
        artist.setName("Test Artist");
        artist.setGender(Gender.MALE);

        Artist savedArtist = new Artist();
        savedArtist.setId(1L);
        savedArtist.setName("Test Artist");
        savedArtist.setAvatar(imageUrl);
        savedArtist.setGender(Gender.MALE);

        ArtistResponse artistResponse = new ArtistResponse();
        artistResponse.setId(1L);
        artistResponse.setName("Test Artist");
        artistResponse.setAvatar(imageUrl);

        when(artistMapper.fromRequestToEntity(request)).thenReturn(artist);
        when(fileService.uploadFile(image, ARTISTS_IMG_FOLDER)).thenReturn(imageUrl);
        when(artistRepository.save(artist)).thenReturn(savedArtist);
        when(artistMapper.fromEntityToResponse(savedArtist)).thenReturn(artistResponse);

        // Act
        ArtistResponse result = artistService.createArtist(request, image);

        // Assert
        assertEquals(artistResponse, result);
        verify(artistMapper).fromRequestToEntity(request);
        verify(fileService).uploadFile(image, ARTISTS_IMG_FOLDER);
        verify(artistRepository).save(artist);
        verify(artistMapper).fromEntityToResponse(savedArtist);
    }

    @DisplayName(value = "Create artist without image successfully")
    @Test
    public void testCreateArtist_ShouldSaveArtist_WhenImageNotProvided() {
        // Arrange
        ArtistRequest request = new ArtistRequest();
        request.setName("Test Artist");
        request.setGender(Gender.MALE);

        Artist artist = new Artist();
        artist.setName("Test Artist");
        artist.setGender(Gender.MALE);

        Artist savedArtist = new Artist();
        savedArtist.setId(1L);
        savedArtist.setName("Test Artist");
        savedArtist.setGender(Gender.MALE);

        ArtistResponse artistResponse = new ArtistResponse();
        artistResponse.setId(1L);
        artistResponse.setName("Test Artist");
        artistResponse.setGender(Gender.MALE);

        when(artistMapper.fromRequestToEntity(request)).thenReturn(artist);
        when(artistRepository.save(artist)).thenReturn(savedArtist);
        when(artistMapper.fromEntityToResponse(savedArtist)).thenReturn(artistResponse);

        // Act
        ArtistResponse result = artistService.createArtist(request, null);

        // Assert
        assertEquals(artistResponse, result);
        verify(artistMapper).fromRequestToEntity(request);
        verify(fileService, never()).uploadFile(any(MultipartFile.class), anyString());
        verify(artistRepository).save(artist);
        verify(artistMapper).fromEntityToResponse(savedArtist);
    }

    @DisplayName(value = "Update artist with image successfully")
    @Test
    public void testUpdateArtist_ShouldUpdateArtist_WhenImageProvided() {
        // Arrange
        Long artistId = 1L;
        ArtistRequest request = new ArtistRequest();
        request.setName("Updated Artist");
        request.setGender(Gender.FEMALE);

        MultipartFile image = mock(MultipartFile.class);
        String imageUrl = "updated-artist.jpg";

        Artist existingArtist = new Artist();
        existingArtist.setId(artistId);
        existingArtist.setName("Old Artist");
        existingArtist.setGender(Gender.MALE);

        Artist updatedArtist = new Artist();
        updatedArtist.setId(artistId);
        updatedArtist.setName("Updated Artist");
        updatedArtist.setAvatar(imageUrl);
        updatedArtist.setGender(Gender.FEMALE);

        ArtistResponse artistResponse = new ArtistResponse();
        artistResponse.setId(artistId);
        artistResponse.setName("Updated Artist");
        artistResponse.setAvatar(imageUrl);
        artistResponse.setGender(Gender.FEMALE);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));
        when(fileService.uploadFile(image, ARTISTS_IMG_FOLDER)).thenReturn(imageUrl);
        when(artistRepository.save(existingArtist)).thenReturn(updatedArtist);
        when(artistMapper.fromEntityToResponse(updatedArtist)).thenReturn(artistResponse);

        // Act
        ArtistResponse result = artistService.updateArtist(artistId, request, image);

        // Assert
        assertEquals(artistResponse, result);
        verify(artistRepository).findById(artistId);
        verify(fileService).uploadFile(image, ARTISTS_IMG_FOLDER);
        verify(artistRepository).save(existingArtist);
        verify(artistMapper).fromEntityToResponse(updatedArtist);
    }

    @DisplayName(value = "Update artist without image successfully")
    @Test
    public void testUpdateArtist_ShouldUpdateArtist_WhenImageNotProvided() {
        // Arrange
        Long artistId = 1L;
        ArtistRequest request = new ArtistRequest();
        request.setName("Updated Artist");
        request.setGender(com.xdpsx.music.model.enums.Gender.FEMALE);

        Artist existingArtist = new Artist();
        existingArtist.setId(artistId);
        existingArtist.setName("Old Artist");
        existingArtist.setGender(com.xdpsx.music.model.enums.Gender.MALE);

        Artist updatedArtist = new Artist();
        updatedArtist.setId(artistId);
        updatedArtist.setName("Updated Artist");

        ArtistResponse artistResponse = new ArtistResponse();
        artistResponse.setId(artistId);
        artistResponse.setName("Updated Artist");

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(existingArtist));
        when(artistRepository.save(existingArtist)).thenReturn(updatedArtist);
        when(artistMapper.fromEntityToResponse(updatedArtist)).thenReturn(artistResponse);

        // Act
        ArtistResponse result = artistService.updateArtist(artistId, request, null);

        // Assert
        assertEquals(artistResponse, result);
        verify(artistRepository).findById(artistId);
        verify(fileService, never()).uploadFile(any(MultipartFile.class), anyString());
        verify(artistRepository).save(existingArtist);
        verify(artistMapper).fromEntityToResponse(updatedArtist);
    }

    @DisplayName(value = "Update artist that does not exist")
    @Test
    public void testUpdateArtist_ShouldThrowResourceNotFoundException_WhenArtistDoesNotExist() {
        Long artistId = 1L;
        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());
        when(i18nUtils.getArtistNotFoundMsg(artistId)).thenReturn("Artist not found");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> artistService.updateArtist(artistId, any(ArtistRequest.class), null));

        assertEquals("Artist not found", exception.getMessage());
        verify(artistRepository).findById(artistId);
        verify(artistRepository, never()).save(any());
        verify(artistMapper, never()).fromEntityToResponse(any());
    }

    @DisplayName(value = "Delete artist successfully")
    @Test
    public void testDeleteArtist_ShouldDeleteArtist_WhenArtistExists() {
        // Arrange
        Long artistId = 1L;
        Artist artist = new Artist();
        artist.setId(artistId);
        artist.setAvatar("artist.jpg");

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));

        // Act
        artistService.deleteArtist(artistId);

        // Assert
        verify(artistRepository).findById(artistId);
        verify(fileService).deleteFileByUrl(artist.getAvatar());
        verify(artistRepository).delete(artist);
    }

    @DisplayName(value = "Delete artist that does not exist")
    @Test
    public void testDeleteArtist_ShouldThrowResourceNotFoundException_WhenArtistDoesNotExists() {
        // Arrange
        Long artistId = 1L;

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());
        when(i18nUtils.getArtistNotFoundMsg(artistId)).thenReturn("Artist not found");

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> artistService.deleteArtist(artistId));

        assertEquals("Artist not found", exception.getMessage());
        verify(artistRepository).findById(artistId);
        verify(artistRepository, never()).delete(any());
        verify(fileService, never()).deleteFileByUrl(anyString());
    }
}