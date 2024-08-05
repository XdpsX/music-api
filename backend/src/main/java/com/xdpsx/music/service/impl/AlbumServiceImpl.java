package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.params.AlbumParams;
import com.xdpsx.music.dto.request.AlbumRequest;
import com.xdpsx.music.dto.response.AlbumResponse;
import com.xdpsx.music.model.entity.Album;
import com.xdpsx.music.model.entity.Artist;
import com.xdpsx.music.model.entity.Genre;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.AlbumMapper;
import com.xdpsx.music.repository.AlbumRepository;
import com.xdpsx.music.repository.ArtistRepository;
import com.xdpsx.music.repository.GenreRepository;
import com.xdpsx.music.repository.TrackRepository;
import com.xdpsx.music.service.AlbumService;
import com.xdpsx.music.service.FileService;
import com.xdpsx.music.util.Compare;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.xdpsx.music.constant.FileConstants.ALBUMS_IMG_FOLDER;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumMapper albumMapper;
    private final FileService fileService;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;

    @Override
    public AlbumResponse createAlbum(AlbumRequest request, MultipartFile image) {
        Album album = albumMapper.fromRequestToEntity(request);

        // Image
        if (image != null){
            String imageUrl = fileService.uploadFile(image, ALBUMS_IMG_FOLDER);
            album.setImage(imageUrl);
        }

        // Genre
        Genre genre = fetchGenreById(request.getGenreId());
        album.setGenre(genre);

        // Artist
        List<Artist> artists = getArtistsByIds(request.getArtistIds());
        album.setArtists(artists);

        Album savedAlbum = albumRepository.save(album);
        return albumMapper.fromEntityToResponse(savedAlbum);
    }

    @Override
    public AlbumResponse updateAlbum(Long id, AlbumRequest request, MultipartFile newImage) {
        Album album = fetchAlbumById(id);
        album.updateAlbum(request);

        // Image
        String oldImage = null;
        if (newImage != null){
            oldImage = album.getImage();
            String newImageUrl = fileService.uploadFile(newImage, ALBUMS_IMG_FOLDER);
            album.setImage(newImageUrl);
        }

        // Genre
        if (!album.getGenre().getId().equals(request.getGenreId())){
            Genre newGenre = fetchGenreById(request.getGenreId());
            album.setGenre(newGenre);
        }

        // Artist
        if (!Compare.isSameArtists(album.getArtists(), request.getArtistIds())){
            List<Artist> newArtists = getArtistsByIds(request.getArtistIds());
            album.setArtists(newArtists);
        }

        Album updatedAlbum = albumRepository.save(album);
        if (oldImage != null){
            fileService.deleteFileByUrl(oldImage);
        }
        return this.mapToResponse(updatedAlbum);
    }

    @Override
    public AlbumResponse getAlbumById(Long id) {
        Album album = fetchAlbumById(id);
        return this.mapToResponse(album);
    }

    @Override
    public PageResponse<AlbumResponse> getAllAlbums(AlbumParams params) {
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Album> albumPage = albumRepository.findAlbumsWithFilters(
                pageable, params.getSearch(), params.getSort()
        );
        return getAlbumResponses(albumPage);
    }

    @Override
    public void deleteAlbum(Long id) {
        Album album = fetchAlbumById(id);
        fileService.deleteFileByUrl(album.getImage());
        albumRepository.delete(album);
    }

    @Override
    public PageResponse<AlbumResponse> getAlbumsByGenreId(Integer genreId, AlbumParams params) {
        Genre genre = fetchGenreById(genreId);

        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Album> albumPage = albumRepository.findAlbumsWithGenreFilters(
                pageable, params.getSearch(), params.getSort(), genre.getId()
        );
        return getAlbumResponses(albumPage);
    }

    private Genre fetchGenreById(Integer genreId) {
        return genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found genre with ID=%s", genreId)));
    }

    @Override
    public PageResponse<AlbumResponse> getAlbumsByArtistId(Long artistId, AlbumParams params) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found artist with ID=%s", artistId)));
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Album> albumPage = albumRepository.findAlbumsWithArtistFilters(
                pageable, params.getSearch(), params.getSort(), artist.getId()
        );
        return getAlbumResponses(albumPage);
    }

    private Album fetchAlbumById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found album with ID=%s", id)));
    }

    private List<Artist> getArtistsByIds(List<Long> artistIds) {
        return artistIds.stream()
                .map(artistId -> artistRepository.findById(artistId)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found artist with ID=%s", artistId))))
                .collect(Collectors.toList());
    }

    private AlbumResponse mapToResponse(Album album){
        AlbumResponse response = albumMapper.fromEntityToResponse(album);
        int totalTracks = trackRepository.countByAlbumId(album.getId());
        response.setTotalTracks(totalTracks);
        return response;
    }

    private PageResponse<AlbumResponse> getAlbumResponses(Page<Album> albumPage) {
        List<AlbumResponse> responses = albumPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return PageResponse.<AlbumResponse>builder()
                .items(responses)
                .pageNum(albumPage.getNumber() + 1)
                .pageSize(albumPage.getSize())
                .totalItems(albumPage.getTotalElements())
                .totalPages(albumPage.getTotalPages())
                .build();
    }

}
