package com.xdpsx.music.service.impl;

import com.xdpsx.music.constant.Keys;
import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.TrackRequest;
import com.xdpsx.music.dto.request.params.TrackParams;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.mapper.PageMapper;
import com.xdpsx.music.model.entity.*;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.TrackMapper;
import com.xdpsx.music.repository.*;
import com.xdpsx.music.security.UserContext;
import com.xdpsx.music.service.CacheService;
import com.xdpsx.music.service.FileService;
import com.xdpsx.music.service.TrackService;
import com.xdpsx.music.util.Compare;
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

import java.util.List;
import java.util.stream.Collectors;

import static com.xdpsx.music.constant.FileConstants.*;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {
    private final TrackMapper trackMapper;
    private final PageMapper pageMapper;
    private final FileService fileService;
    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final ArtistRepository artistRepository;
    private final PlaylistRepository playlistRepository;
    private final UserContext userContext;
    private final CacheService cacheService;
    private final I18nUtils i18nUtils;

    @Override
    @CachePut(value = Keys.TRACK_ITEM, key = "#result.id")
    @Caching(evict = {
            @CacheEvict(value = Keys.TRACKS, allEntries = true),
            @CacheEvict(value = Keys.GENRE_TRACKS, allEntries = true),
            @CacheEvict(value = Keys.ARTIST_TRACKS, allEntries = true),
            @CacheEvict(value = Keys.ALBUM_TRACKS, allEntries = true)
    })
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
    @CachePut(value = Keys.TRACK_ITEM, key = "#result.id")
    @Caching(evict = {
            @CacheEvict(value = Keys.TRACKS, allEntries = true),
            @CacheEvict(value = Keys.GENRE_TRACKS, allEntries = true),
            @CacheEvict(value = Keys.ARTIST_TRACKS, allEntries = true),
            @CacheEvict(value = Keys.ALBUM_TRACKS, allEntries = true)
    })
    public TrackResponse updateTrack(Long id, TrackRequest request, MultipartFile newImage, MultipartFile newFile) {
        Track trackToUpdate = getTrack(id);
        updateTrackDetails(trackToUpdate, request, newImage, newFile);

        Track updatedTrack = trackRepository.save(trackToUpdate);
        deleteOldFiles(trackToUpdate, newImage, newFile);

        return trackMapper.fromEntityToResponse(updatedTrack);
    }

    @Override
    @Cacheable(value = Keys.TRACK_ITEM, key = "#id")
    public TrackResponse getTrackById(Long id) {
        Track track = getTrack(id);
        return trackMapper.fromEntityToResponse(track);
    }

    @Override
    @Cacheable(cacheNames = Keys.TRACKS, key = "#params")
    public PageResponse<TrackResponse> getAllTracks(TrackParams params) {
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Track> trackPage = trackRepository.findWithFilters(
                pageable, params.getSearch(), params.getSort()
        );
        return pageMapper.toTrackPageResponse(trackPage);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = Keys.TRACK_ITEM, key = "#id"),
            @CacheEvict(value = Keys.TRACKS, allEntries = true),
            @CacheEvict(value = Keys.GENRE_TRACKS, allEntries = true),
            @CacheEvict(value = Keys.ARTIST_TRACKS, allEntries = true),
            @CacheEvict(value = Keys.ALBUM_TRACKS, allEntries = true)
    })
    public void deleteTrack(Long id) {
        Track trackToDelete = getTrack(id);
        trackRepository.delete(trackToDelete);
        deleteFiles(trackToDelete);
    }

    @Override
    @Cacheable(cacheNames = Keys.GENRE_TRACKS, key = "#genreId + '_' + #params")
    public PageResponse<TrackResponse> getTracksByGenreId(Integer genreId, TrackParams params) {
        Genre genre = getGenre(genreId);
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Track> trackPage = trackRepository.findTracksByGenre(
                pageable, params.getSearch(), params.getSort(), genre.getId()
        );
        return pageMapper.toTrackPageResponse(trackPage);
    }

    @Override
    @Cacheable(cacheNames = Keys.ARTIST_TRACKS, key = "#artistId + '_' + #params")
    public PageResponse<TrackResponse> getTracksByArtistId(Long artistId, TrackParams params) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getArtistNotFoundMsg(artistId)));

        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Track> trackPage = trackRepository.findTracksByArtist(
                pageable, params.getSearch(), params.getSort(), artist.getId()
        );
        return pageMapper.toTrackPageResponse(trackPage);
    }

    @Override
    @Cacheable(cacheNames = Keys.ALBUM_TRACKS, key = "#albumId + '_' + #params")
    public PageResponse<TrackResponse> getTracksByAlbumId(Long albumId, TrackParams params) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getAlbumNotFoundMsg(albumId)));
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Track> trackPage = trackRepository.findTracksByAlbum(
                pageable, params.getSearch(), params.getSort(), album.getId()
        );
        return pageMapper.toTrackPageResponse(trackPage);
    }

    @Override
    public PageResponse<TrackResponse> getLikedTracks(TrackParams params, User loggedUser) {
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Track> trackPage = trackRepository.findFavoriteTracksByUserId(
                pageable, params.getSearch(), params.getSort(), loggedUser.getId()
        );
        return pageMapper.toTrackPageResponse(trackPage);
    }

    @Override
    public PageResponse<TrackResponse> getTracksByPlaylist(Long playlistId, TrackParams params) {
        User loggedUser = userContext.getLoggedUser();
        Playlist playlist = playlistRepository.findByIdAndOwnerId(playlistId, loggedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getPlaylistNotFoundMsg(playlistId)));

        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Track> trackPage = trackRepository.findTracksInPlaylist(
                pageable, params.getSearch(), params.getSort(), playlist.getId()
        );
        return pageMapper.toTrackPageResponse(trackPage);
    }

    @CacheEvict(cacheNames = Keys.TRACK_ITEM, key = "#trackId")
    @Override
    @Transactional
    public void incrementListeningCount(Long trackId, User loggedUser) {
        Track track = getTrack(trackId);
        String listeningKey = Keys.getListeningKey(loggedUser.getId());
        boolean exists = cacheService.hasKey(listeningKey);
        if (!exists){
            track.setListeningCount(track.getListeningCount() + 1);
            trackRepository.save(track);

            cacheService.setValue(listeningKey, "1", track.getDurationMs());
        }

    }

    private List<Artist> getArtists(List<Long> artistIds) {
        return artistIds.stream()
                .map(artistId -> artistRepository.findById(artistId)
                        .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getArtistNotFoundMsg(artistId))))
                .collect(Collectors.toList());
    }

    private void setTrackNumber(Track track, Album album) {
        int trackNumber = trackRepository.countByAlbumId(album.getId());
        track.setTrackNumber(trackNumber+1);
    }

    private Genre getGenre(Integer genreId) {
        return genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getGenreNotFoundMsg(genreId)));
    }

    private Album getAlbumIfExists(Long albumId) {
        return albumId == null ? null : albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getAlbumNotFoundMsg(albumId)));
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

    private Album getAlbum(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getAlbumNotFoundMsg(albumId)));
    }


    private Track getTrack(Long trackId) {
        return trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getTrackNotFoundMsg(trackId)));
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

}
