package com.xdpsx.music.service.impl;

import com.xdpsx.music.exception.BadRequestException;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.model.entity.Playlist;
import com.xdpsx.music.model.entity.PlaylistTrack;
import com.xdpsx.music.model.entity.Track;
import com.xdpsx.music.model.entity.User;
import com.xdpsx.music.model.id.PlaylistTrackId;
import com.xdpsx.music.repository.PlaylistRepository;
import com.xdpsx.music.repository.PlaylistTrackRepository;
import com.xdpsx.music.repository.TrackRepository;
import com.xdpsx.music.security.UserContext;
import com.xdpsx.music.service.PlaylistTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PlaylistTrackServiceImpl implements PlaylistTrackService {
    private final UserContext userContext;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;

    @Transactional
    @Override
    public void addTrackToPlaylist(Long playlistId, Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Not found track with ID=%s", trackId)
                ));
        User loggedUser = userContext.getLoggedUser();
        Playlist playlist = playlistRepository.findByIdAndOwnerId(playlistId, loggedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Playlist with id=%s not found", playlistId)
                ));

        PlaylistTrackId id = new PlaylistTrackId(playlistId, trackId);
        if (playlistTrackRepository.existsById(id)) {
            throw new BadRequestException(
                    String.format("Track with id=%s is already in playlist", trackId)
            );
        }

        int trackNumber = playlistTrackRepository.countByPlaylistId(playlistId) + 1;
        PlaylistTrack playlistTrack = PlaylistTrack.builder()
                .id(id)
                .playlist(playlist)
                .track(track)
                .trackNumber(trackNumber)
                .build();
        playlistTrackRepository.save(playlistTrack);

        playlist.setUpdatedAt(LocalDateTime.now());
        playlistRepository.save(playlist);
    }

    @Override
    public void removeTrackFromPlaylist(Long playlistId, Long trackId) {
        if (!trackRepository.existsById(trackId)){
            throw new ResourceNotFoundException(String.format("Not found track with ID=%s", trackId));
        }
        User loggedUser = userContext.getLoggedUser();
        playlistRepository.findByIdAndOwnerId(playlistId, loggedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Playlist with id=%s not found", playlistId)
        ));

        PlaylistTrackId id = new PlaylistTrackId(playlistId, trackId);
        if (!playlistTrackRepository.existsById(id)) {
            throw new BadRequestException(
                    String.format("Track with id=%s is not in playlist", trackId)
            );
        }

        playlistTrackRepository.deleteById(id);

    }
}
