package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.TrackRequest;
import com.xdpsx.music.dto.request.params.TrackParams;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.model.entity.*;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.TrackMapper;
import com.xdpsx.music.repository.*;
import com.xdpsx.music.security.UserContext;
import com.xdpsx.music.service.FileService;
import com.xdpsx.music.service.TrackService;
import com.xdpsx.music.util.Compare;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.xdpsx.music.constant.FileConstants.*;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {
    private final TrackMapper trackMapper;
    private final FileService fileService;
    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;
    private final PlaylistRepository playlistRepository;
    private final UserContext userContext;

    @Override
    public TrackResponse createTrack(TrackRequest request, MultipartFile image, MultipartFile file) {
        Track track = trackMapper.fromRequestToEntity(request);

        // Track could belong to an Album or not
        Album album = getAlbumIfExists(request.getAlbumId());
        if (album != null) {
            setTrackNumber(track, album);
        }
        // Get Genre and Artists
        Genre genre = getGenre(request.getGenreId());
        List<Artist> artists = getArtists(request.getArtistIds());

        track.setAlbum(album);
        track.setGenre(genre);
        track.setArtists(artists);

        // Image and File
        track.setImage(uploadFile(image, TRACKS_IMG_FOLDER));
        track.setUrl(uploadFile(file, TRACKS_FILE_FOLDER));

        Track savedTrack = trackRepository.save(track);
        return trackMapper.fromEntityToResponse(savedTrack);
    }

    @Transactional
    @Override
    public TrackResponse updateTrack(Long id, TrackRequest request, MultipartFile newImage, MultipartFile newFile) {
        Track trackToUpdate = getTrack(id);
        updateTrackDetails(trackToUpdate, request, newImage, newFile);

        Track updatedTrack = trackRepository.save(trackToUpdate);
        deleteOldFiles(trackToUpdate, newImage, newFile);

        return this.mapToResponse(updatedTrack);
    }

    @Override
    public TrackResponse getTrackById(Long id) {
        Track track = getTrack(id);
        return this.mapToResponse(track);
    }

    @Override
    public PageResponse<TrackResponse> getAllTracks(TrackParams params) {
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Track> trackPage = trackRepository.findWithFilters(
                pageable, params.getSearch(), params.getSort()
        );
        return getTrackResponses(trackPage);
    }

    @Transactional
    @Override
    public void deleteTrack(Long id) {
        Track trackToDelete = getTrack(id);

        Long albumId = trackToDelete.getAlbum().getId();
        Integer deletedTrackNumber = trackToDelete.getTrackNumber();

        trackRepository.delete(trackToDelete);
        adjustTrackNumbers(albumId, deletedTrackNumber);

        deleteFiles(trackToDelete);
    }

    @Override
    public PageResponse<TrackResponse> getTracksByGenreId(Integer genreId, TrackParams params) {
        Genre genre = getGenre(genreId);
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Track> trackPage = trackRepository.findWithGenreFilters(
                pageable, params.getSearch(), params.getSort(), genre.getId()
        );
        return getTrackResponses(trackPage);
    }

    @Override
    public PageResponse<TrackResponse> getTracksByArtistId(Long artistId, TrackParams params) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found artist with ID=%s", artistId)));

        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Track> trackPage = trackRepository.findWithArtistFilters(
                pageable, params.getSearch(), params.getSort(), artist.getId()
        );
        return getTrackResponses(trackPage);
    }

    @Override
    public PageResponse<TrackResponse> getTracksByAlbumId(Long albumId, TrackParams params) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found album with ID=%s", albumId)));
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Track> trackPage = trackRepository.findWithAlbumFilters(
                pageable, params.getSearch(), params.getSort(), album.getId()
        );
        return getTrackResponses(trackPage);
    }

    @Override
    public PageResponse<TrackResponse> getLikedTracks(TrackParams params, User loggedUser) {
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Track> trackPage = trackRepository.findLikedTracksByUserId(
                loggedUser.getId(), pageable, params.getSearch(), params.getSort()
        );
        return getTrackResponses(trackPage);
    }

    @Override
    public PageResponse<TrackResponse> getTracksByPlaylist(Long playlistId, TrackParams params) {
        User loggedUser = userContext.getLoggedUser();
        Playlist playlist = playlistRepository.findByIdAndOwnerId(playlistId, loggedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Playlist with id=%s not found", playlistId)
                ));

        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Track> trackPage = trackRepository.findTracksInPlaylist(
                pageable, params.getSearch(), params.getSort(), playlistId
        );
        return getTrackResponses(trackPage);
    }

    private TrackResponse mapToResponse(Track track){
        TrackResponse response = trackMapper.fromEntityToResponse(track);
        long totalLikes = trackRepository.countLikesByTrackId(track.getId());
        response.setTotalLikes(totalLikes);
        return response;
    }

    private List<Artist> getArtists(List<Long> artistIds) {
        return artistIds.stream()
                .map(artistId -> artistRepository.findById(artistId)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found artist with ID=%s", artistId))))
                .collect(Collectors.toList());
    }

    private void setTrackNumber(Track track, Album album) {
        int trackNumber = trackRepository.countByAlbumId(album.getId());
        track.setTrackNumber(trackNumber+1);
    }

    private Genre getGenre(Integer request) {
        return genreRepository.findById(request)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found genre with ID=%s", request)));
    }

    private Album getAlbumIfExists(Long albumId) {
        return albumId == null ? null : albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found album with ID=%s", albumId)));
    }

    private void updateTrackDetails(Track trackToUpdate, TrackRequest request, MultipartFile newImage, MultipartFile newFile) {
        trackToUpdate.setName(request.getName());

        if (isAlbumUpdateNeeded(trackToUpdate, request.getAlbumId())) {
            handleAlbumUpdate(trackToUpdate, request.getAlbumId());
        }

        if (!trackToUpdate.getGenre().getId().equals(request.getGenreId())) {
            trackToUpdate.setGenre(getGenre(request.getGenreId()));
        }

        if (!Compare.isSameArtists(trackToUpdate.getArtists(), request.getArtistIds())) {
            trackToUpdate.setArtists(getArtists(request.getArtistIds()));
        }

        if (newImage != null) {
            trackToUpdate.setImage(uploadFile(newImage, TRACKS_IMG_FOLDER));
        }

        if (newFile != null) {
            trackToUpdate.setUrl(uploadFile(newFile, TRACKS_FILE_FOLDER));
            trackToUpdate.setDurationMs(request.getDurationMs());
        }
    }

    private boolean isAlbumUpdateNeeded(Track trackToUpdate, Long albumId) {
        return (trackToUpdate.getAlbum() == null) ? (albumId != null) : !trackToUpdate.getAlbum().getId().equals(albumId);
    }

    private void handleAlbumUpdate(Track trackToUpdate, Long newAlbumId) {
        Album oldAlbum = trackToUpdate.getAlbum();
        Integer deletedTrackNumber = trackToUpdate.getTrackNumber();

        if (oldAlbum != null) {
            adjustTrackNumbers(oldAlbum.getId(), deletedTrackNumber);
        }

        if (newAlbumId != null) {
            Album newAlbum = getAlbum(newAlbumId);
            int newTrackNumber = trackRepository.countByAlbumId(newAlbum.getId());
            trackToUpdate.setTrackNumber(newTrackNumber + 1);
            trackToUpdate.setAlbum(newAlbum);
        } else {
            trackToUpdate.setTrackNumber(null);
            trackToUpdate.setAlbum(null);
        }
    }

    private void adjustTrackNumbers(Long albumId, Integer deletedTrackNumber) {
        List<Track> tracks = trackRepository.findByAlbumIdOrderByTrackNumberAsc(albumId);
        tracks.stream()
                .filter(track -> track.getTrackNumber() > deletedTrackNumber)
                .forEach(track -> track.setTrackNumber(track.getTrackNumber() - 1));
        trackRepository.saveAll(tracks);
    }

    private Album getAlbum(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found album with ID=%s", albumId)));
    }


    private Track getTrack(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found track with ID=%s", id)));
    }

    private String uploadFile(MultipartFile file, String folder) {
        return file != null ? fileService.uploadFile(file, folder) : null;
    }

    private void deleteOldFiles(Track trackToUpdate, MultipartFile newImage, MultipartFile newFile) {
        if (newImage != null) {
            fileService.deleteFileByUrl(trackToUpdate.getImage());
        }
        if (newFile != null) {
            fileService.deleteFileByUrl(trackToUpdate.getUrl());
        }
    }

    private void deleteFiles(Track track) {
        fileService.deleteFileByUrl(track.getImage());
        fileService.deleteFileByUrl(track.getUrl());
    }

    private PageResponse<TrackResponse> getTrackResponses(Page<Track> trackPage) {
        List<TrackResponse> responses = trackPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return PageResponse.<TrackResponse>builder()
                .items(responses)
                .pageNum(trackPage.getNumber() + 1)
                .pageSize(trackPage.getSize())
                .totalItems(trackPage.getTotalElements())
                .totalPages(trackPage.getTotalPages())
                .build();
    }
}
