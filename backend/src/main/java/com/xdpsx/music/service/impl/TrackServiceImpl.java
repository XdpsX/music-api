package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.request.TrackRequest;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.entity.Album;
import com.xdpsx.music.entity.Artist;
import com.xdpsx.music.entity.Genre;
import com.xdpsx.music.entity.Track;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.TrackMapper;
import com.xdpsx.music.repository.AlbumRepository;
import com.xdpsx.music.repository.ArtistRepository;
import com.xdpsx.music.repository.GenreRepository;
import com.xdpsx.music.repository.TrackRepository;
import com.xdpsx.music.service.FileService;
import com.xdpsx.music.service.TrackService;
import com.xdpsx.music.util.Compare;
import lombok.RequiredArgsConstructor;
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

    @Override
    public TrackResponse createTrack(TrackRequest request, MultipartFile image, MultipartFile file) {
        // Track could belong to an Album or not
        Album album = null;
        if (request.getAlbumId() != null){
            album = albumRepository.findById(request.getAlbumId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found album with ID=%s", request.getAlbumId())));
        }

        // Get Genre and Artists
        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found genre with ID=%s", request.getGenreId())));
        List<Artist> artists = request.getArtistIds().stream()
                .map(artistId -> artistRepository.findById(artistId)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found artist with ID=%s", artistId))))
                .collect(Collectors.toList());
        Track track = trackMapper.fromRequestToEntity(request);
        track.setAlbum(album);
        track.setGenre(genre);
        track.setArtists(artists);

        // Image
        if (image != null){
            String imageUrl = fileService.uploadFile(image, TRACKS_IMG_FOLDER);
            track.setImage(imageUrl);
        }
        // File
        String fileUrl = fileService.uploadFile(file, TRACKS_FILE_FOLDER);
        track.setUrl(fileUrl);

        int trackNumber = trackRepository.countByAlbumId(album.getId());
        track.setTrackNumber(trackNumber+1);

        Track savedTrack = trackRepository.save(track);
        return trackMapper.fromEntityToResponse(savedTrack);
    }

    @Override
    public TrackResponse updateTrack(Long id, TrackRequest request, MultipartFile newImage, MultipartFile newFile) {
        Track trackToUpdate= trackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found track with ID=%s", id)));
        trackToUpdate.setName(request.getName());

        // Album
        if (isUpdateAlbumInTrack(trackToUpdate, request.getAlbumId())){
            Album newAlbum = albumRepository.findById(request.getAlbumId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(String.format("Not found album with ID=%s", request.getGenreId()))
                    );
            trackToUpdate.setAlbum(newAlbum);
        }

        // Genre
        if (!trackToUpdate.getGenre().getId().equals(request.getGenreId())){
            Genre newGenre = genreRepository.findById(request.getGenreId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(String.format("Not found genre with ID=%s", request.getGenreId()))
                    );
            trackToUpdate.setGenre(newGenre);
        }

        // Artist
        if (!Compare.isSameArtists(trackToUpdate.getArtists(), request.getArtistIds())){
            List<Artist> newArtists = request.getArtistIds().stream()
                    .map(artistId -> artistRepository.findById(artistId)
                            .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found artist with ID=%s", artistId))))
                    .collect(Collectors.toList());
            trackToUpdate.setArtists(newArtists);
        }

        // Image
        String oldImage = null;
        if (newImage != null){
            oldImage = trackToUpdate.getImage();
            String newImageUrl = fileService.uploadFile(newImage, TRACKS_IMG_FOLDER);
            trackToUpdate.setImage(newImageUrl);
        }

        // File and Duration
        String oldFile = null;
        if (newFile != null){
            oldFile = trackToUpdate.getUrl();
            String newFileUrl = fileService.uploadFile(newFile, TRACKS_FILE_FOLDER);
            trackToUpdate.setDurationMs(request.getDurationMs());
            trackToUpdate.setUrl(newFileUrl);
        }

        Track updatedTrack = trackRepository.save(trackToUpdate);

        // Delete old image and file if they exist
        if (oldImage != null){
            fileService.deleteFileByUrl(oldImage);
        }
        if (oldFile != null){
            fileService.deleteFileByUrl(oldImage);
        }

        return trackMapper.fromEntityToResponse(updatedTrack);
    }

    private boolean isUpdateAlbumInTrack(Track trackToUpdate, Long albumId){
        if (trackToUpdate.getAlbum() == null && albumId != null){
            return true;
        }else if (trackToUpdate.getAlbum() != null){
            if (!trackToUpdate.getAlbum().getId().equals(albumId)){
                return true;
            }
        }
        return false;
    }

    @Override
    public TrackResponse getTrackById(Long id) {
        Track track = trackRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Not found track with ID=%s", id)));
        return trackMapper.fromEntityToResponse(track);
    }

    @Override
    public List<TrackResponse> getAllTracks() {
        return trackRepository.findAll()
                .stream()
                .map(trackMapper::fromEntityToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteTrack(Long id) {
        Track trackToDelete = trackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Not found track with ID=%s", id)));

        Long albumId = trackToDelete.getAlbum().getId();
        Integer deletedTrackNumber = trackToDelete.getTrackNumber();
        String imageToDelete = trackToDelete.getImage();
        String urlToDelete = trackToDelete.getUrl();

        trackRepository.delete(trackToDelete);

        // Adjust trackNumber of remaining tracks
        List<Track> tracks = trackRepository.findByAlbumIdOrderByTrackNumberAsc(albumId);
        for (Track track : tracks) {
            if (track.getTrackNumber() > deletedTrackNumber) {
                track.setTrackNumber(track.getTrackNumber() - 1);
            }
        }
        trackRepository.saveAll(tracks);

        // Delete image and file
        fileService.deleteFileByUrl(imageToDelete);
        fileService.deleteFileByUrl(urlToDelete);
    }
}
