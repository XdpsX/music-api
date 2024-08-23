package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.request.GenreRequest;
import com.xdpsx.music.dto.response.GenreResponse;
import com.xdpsx.music.exception.DuplicateResourceException;
import com.xdpsx.music.model.entity.Genre;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.GenreMapper;
import com.xdpsx.music.repository.GenreRepository;
import com.xdpsx.music.service.FileService;
import com.xdpsx.music.service.GenreService;
import com.xdpsx.music.util.I18nUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.xdpsx.music.constant.FileConstants.GENRES_IMG_FOLDER;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreMapper genreMapper;
    private final FileService fileService;
    private final GenreRepository genreRepository;
    private final I18nUtils i18nUtils;

    @Override
    public GenreResponse createGenre(GenreRequest request, MultipartFile image) {
            if (genreRepository.existsByName(request.getName())){
                throw new DuplicateResourceException(i18nUtils.getGenreExistsMsg(request.getName()));
            }
            Genre genre = genreMapper.fromRequestToEntity(request);
            if (image != null){
                String imageUrl = fileService.uploadFile(image, GENRES_IMG_FOLDER);
                genre.setImage(imageUrl);
            }
            Genre createdGenre = genreRepository.save(genre);
            return genreMapper.fromEntityToResponse(createdGenre);
    }

    @Override
    public List<GenreResponse> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return genres.stream()
                .map(genreMapper::fromEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteGenre(Integer genreId) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getGenreNotFoundMsg(genreId)));
        fileService.deleteFileByUrl(genre.getImage());
        genreRepository.delete(genre);
    }
}
