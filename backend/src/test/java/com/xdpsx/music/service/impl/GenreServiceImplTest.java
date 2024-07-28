package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.request.GenreRequest;
import com.xdpsx.music.dto.response.GenreResponse;
import com.xdpsx.music.entity.Genre;
import com.xdpsx.music.exception.BadRequestException;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.GenreMapper;
import com.xdpsx.music.repository.GenreRepository;
import com.xdpsx.music.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {
    @Mock
    private GenreMapper genreMapper;

    @Mock
    private FileService fileService;

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    private GenreRequest genreRequest;
    private Genre genre;
    private GenreResponse genreResponse;
    private MultipartFile image;

    @BeforeEach
    void setUp() {
        genreRequest = new GenreRequest();
        genreRequest.setName("Pop");

        genre = new Genre();
        genre.setId(1);
        genre.setName("Pop");
        genre.setImage("pop.jpg");

        genreResponse = new GenreResponse();
        genreResponse.setId(1);
        genreResponse.setName("Pop");
        genreResponse.setImage("pop.jpg");

        image = mock(MultipartFile.class);
    }

    @Test
    public void testCreateGenre_whenProvideGenre_returnGenreResponse(){
        when(genreRepository.existsByName(genreRequest.getName())).thenReturn(false);
        when(genreMapper.fromRequestToEntity(genreRequest)).thenReturn(genre);
        when(fileService.uploadFile(image, "genres")).thenReturn("pop.jpg");
        when(genreRepository.save(any(Genre.class))).thenReturn(genre);
        when(genreMapper.fromEntityToResponse(genre)).thenReturn(genreResponse);

        GenreResponse result = genreService.createGenre(genreRequest, image);

        assertNotNull(result);
        assertEquals(genreResponse, result);
        verify(genreRepository).existsByName(genreRequest.getName());
        verify(genreMapper).fromRequestToEntity(genreRequest);
        verify(fileService).uploadFile(image, "genres");
        verify(genreRepository).save(any(Genre.class));
        verify(genreMapper).fromEntityToResponse(genre);
    }

    @Test
    void testCreateGenre_whenProvideGenreExists_throwBadRequestException() {
        when(genreRepository.existsByName(genreRequest.getName())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> genreService.createGenre(genreRequest, image));

        verify(genreRepository).existsByName(genreRequest.getName());
        verify(genreMapper, never()).fromRequestToEntity(any());
        verify(fileService, never()).uploadFile(any(), any());
        verify(genreRepository, never()).save(any());
    }

    @Test
    void testGetAllGenres_returnAllGenres() {
        List<Genre> genres = Arrays.asList(genre);
        when(genreRepository.findAll()).thenReturn(genres);
        when(genreMapper.fromEntityToResponse(any(Genre.class))).thenReturn(genreResponse);

        List<GenreResponse> result = genreService.getAllGenres();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(genreRepository).findAll();
        verify(genreMapper).fromEntityToResponse(any(Genre.class));
    }

    @Test
    void testDeleteGenre_shouldDeleteSuccessfully() {
        when(genreRepository.findById(genre.getId())).thenReturn(Optional.of(genre));

        genreService.deleteGenre(genre.getId());

        verify(genreRepository).findById(genre.getId());
        verify(fileService).deleteFileByUrl(genre.getImage());
        verify(genreRepository).delete(genre);
    }

    @Test
    void testDeleteGenre_whenGenreNotFound_throwResourceNotFoundException() {
        when(genreRepository.findById(genre.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> genreService.deleteGenre(genre.getId()));

        verify(genreRepository).findById(genre.getId());
        verify(fileService, never()).deleteFileByUrl(any());
        verify(genreRepository, never()).delete(any());
    }
}