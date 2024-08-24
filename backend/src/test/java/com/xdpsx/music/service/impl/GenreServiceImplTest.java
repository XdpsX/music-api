package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.request.GenreRequest;
import com.xdpsx.music.dto.response.GenreResponse;
import com.xdpsx.music.exception.DuplicateResourceException;
import com.xdpsx.music.model.entity.Genre;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.GenreMapper;
import com.xdpsx.music.repository.GenreRepository;
import com.xdpsx.music.service.FileService;
import com.xdpsx.music.util.I18nUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.xdpsx.music.constant.FileConstants.GENRES_IMG_FOLDER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {
    @Mock private GenreMapper genreMapper;
    @Mock private FileService fileService;
    @Mock private GenreRepository genreRepository;
    @Mock private I18nUtils i18nUtils;

    @InjectMocks
    private GenreServiceImpl genreService;

    @DisplayName(value = "Create genre when genre exists")
    @Test
    void testCreateGenre_ShouldThrowDuplicateResourceException_WhenGenreExists() {
        GenreRequest request = new GenreRequest("Rock");
        when(genreRepository.existsByName(request.getName())).thenReturn(true);
        when(i18nUtils.getGenreExistsMsg(request.getName())).thenReturn("Genre already exists");

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> genreService.createGenre(request, null));

        assertEquals("Genre already exists", exception.getMessage());
        verify(genreRepository, never()).save(any(Genre.class));
    }

    @DisplayName(value = "Create genre without image successfully")
    @Test
    void testCreateGenre_ShouldSaveGenre_WhenGenreDoesNotExist() {
        GenreRequest request = new GenreRequest("Pop");
        Genre genre = new Genre();
        genre.setName(request.getName());

        when(genreRepository.existsByName(request.getName())).thenReturn(false);
        when(genreMapper.fromRequestToEntity(request)).thenReturn(genre);
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreMapper.fromEntityToResponse(genre)).thenReturn(new GenreResponse(1, "Pop", null));

        GenreResponse response = genreService.createGenre(request, null);

        assertNotNull(response);
        assertEquals("Pop", response.getName());
        verify(fileService, never()).uploadFile(any(MultipartFile.class), any(String.class));
        verify(genreRepository).save(genre);
    }

    @DisplayName(value = "Create genre with image successfully")
    @Test
    void testCreateGenre_ShouldUploadImage_WhenImageProvided() {
        GenreRequest request = new GenreRequest("Pop");
        Genre genre = new Genre();
        genre.setName(request.getName());
        MultipartFile image = mock(MultipartFile.class);

        when(genreRepository.existsByName(request.getName())).thenReturn(false);
        when(genreMapper.fromRequestToEntity(request)).thenReturn(genre);
        when(fileService.uploadFile(image, GENRES_IMG_FOLDER)).thenReturn("pop.jpg");
        when(genreRepository.save(genre)).thenReturn(genre);
        when(genreMapper.fromEntityToResponse(genre)).thenReturn(new GenreResponse(1, "Pop", "pop.jpg"));

        GenreResponse response = genreService.createGenre(request, image);

        assertNotNull(response);
        assertEquals("Pop", response.getName());
        assertEquals("pop.jpg", response.getImage());
        verify(fileService).uploadFile(image, GENRES_IMG_FOLDER);
        verify(genreRepository).save(genre);
    }

    @DisplayName(value = "Get all genres")
    @Test
    void testGetAllGenres_ShouldReturnListGenreResponses() {
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Pop");
        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Rock");

        when(genreRepository.findAll()).thenReturn(Arrays.asList(genre1, genre2));
        when(genreMapper.fromEntityToResponse(genre1)).thenReturn(new GenreResponse(1, "Pop", null));
        when(genreMapper.fromEntityToResponse(genre2)).thenReturn(new GenreResponse(2, "Rock", null));

        List<GenreResponse> genres = genreService.getAllGenres();

        assertNotNull(genres);
        assertEquals(2, genres.size());
        verify(genreRepository).findAll();
    }

    @DisplayName(value = "Delete genre successfully")
    @Test
    void testDeleteGenre_ShouldDeleteGenre_WhenGenreExists() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Pop");
        genre.setImage("pop.jpg");

        when(genreRepository.findById(genre.getId())).thenReturn(Optional.of(genre));

        genreService.deleteGenre(genre.getId());

        verify(fileService).deleteFileByUrl("pop.jpg");
        verify(genreRepository).delete(genre);
    }

    @DisplayName(value = "Delete genre that does not exist")
    @Test
    void testDeleteGenre_ShouldThrowResourceNotFoundException_WhenGenreDoesNotExist() {
        when(genreRepository.findById(1)).thenReturn(Optional.empty());
        when(i18nUtils.getGenreNotFoundMsg(1)).thenReturn("Genre not found");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> genreService.deleteGenre(1));

        assertEquals("Genre not found", exception.getMessage());
        verify(fileService, never()).deleteFileByUrl(anyString());
        verify(genreRepository, never()).delete(any(Genre.class));
    }

}