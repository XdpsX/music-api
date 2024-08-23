package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.PlaylistRequest;
import com.xdpsx.music.dto.request.params.PlaylistParam;
import com.xdpsx.music.dto.response.PlaylistResponse;
import com.xdpsx.music.dto.response.PlaylistTrackExistsResponse;
import com.xdpsx.music.exception.DuplicateResourceException;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.PageMapper;
import com.xdpsx.music.mapper.PlaylistMapper;
import com.xdpsx.music.model.entity.Playlist;
import com.xdpsx.music.model.entity.PlaylistTrack;
import com.xdpsx.music.model.entity.User;
import com.xdpsx.music.repository.PlaylistRepository;
import com.xdpsx.music.repository.PlaylistTrackRepository;
import com.xdpsx.music.security.UserContext;
import com.xdpsx.music.service.PlaylistService;
import com.xdpsx.music.util.I18nUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    private final UserContext userContext;
    private final PlaylistMapper playlistMapper;
    private final PageMapper pageMapper;
    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final I18nUtils i18nUtils;

    @Override
    public PlaylistResponse createPlaylist(PlaylistRequest request) {
        User loggedUser = userContext.getLoggedUser();
        if (playlistRepository.existsByOwnerIdAndName(loggedUser.getId(), request.getName())){
            throw new DuplicateResourceException(i18nUtils.getPlaylistExistsMsg(request.getName()));
        }
        Playlist playlist = playlistMapper.fromRequestToEntity(request);
        playlist.setOwner(loggedUser);

        Playlist savedPlaylist = playlistRepository.save(playlist);
        return playlistMapper.fromEntityToResponse(savedPlaylist);
    }

    @Override
    public void deletePlaylist(Long playlistId) {
        Playlist playlist = getPlaylist(playlistId);
        playlistRepository.delete(playlist);
    }

    private Playlist getPlaylist(Long playlistId) {
        User loggedUser = userContext.getLoggedUser();
        return playlistRepository.findByIdAndOwnerId(playlistId, loggedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(i18nUtils.getPlaylistNotFoundMsg(playlistId)));
    }

    @Override
    public PageResponse<PlaylistResponse> getAllUserPlaylists(PlaylistParam params) {
        User loggedUser = userContext.getLoggedUser();
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Playlist> playlistPage = playlistRepository.findAllWithFilters(
                pageable, params.getSearch(), params.getSort(), loggedUser.getId()
        );
        return pageMapper.toPlaylistPageResponse(playlistPage);
    }

    @Override
    public PlaylistResponse getPlaylistById(Long id) {
        Playlist playlist = getPlaylist(id);
        return playlistMapper.fromEntityToResponse(playlist);
    }

    @Override
    public PlaylistResponse updatePlaylist(Long id, PlaylistRequest request) {
        Playlist playlist = getPlaylist(id);
        if (!playlist.getName().equals(request.getName())){
            playlist.setName(request.getName());
        }
        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return playlistMapper.fromEntityToResponse(updatedPlaylist);
    }

    @Override
    public List<PlaylistTrackExistsResponse> checkTracksInPlaylists(List<Long> playlistIds, List<Long> trackIds) {
        List<Playlist> userPlaylists = playlistIds.stream()
                .map(this::getPlaylist)
                .toList();

        Map<Long, Set<Long>> playlistTrackMap = new HashMap<>();
        // Fetch and map playlist tracks only for the user's playlists
        if (!userPlaylists.isEmpty()) {
            List<Long> userPlaylistIds = userPlaylists.stream().map(Playlist::getId).toList();
            List<PlaylistTrack> playlistTracks = playlistTrackRepository.findByPlaylistIdInAndTrackIdIn(
                    userPlaylistIds, trackIds);
            for (PlaylistTrack pt : playlistTracks) {
                playlistTrackMap
                        .computeIfAbsent(pt.getPlaylist().getId(), k -> new HashSet<>())
                        .add(pt.getTrack().getId());
            }
        }

        List<PlaylistTrackExistsResponse> responses = new ArrayList<>();
//        for (Long playlistId : playlistIds) {
//            if (!playlistTrackMap.containsKey(playlistId)) {
//                for (Long trackId: trackIds){
//                    responses.add(new PlaylistTrackExistsResponse(playlistId, trackId, false));
//                }
//            }
//            for (Long trackId : trackIds) {
//                boolean exists = playlistTrackMap.containsKey(playlistId) && playlistTrackMap.get(playlistId).contains(trackId);
//                responses.add(new PlaylistTrackExistsResponse(playlistId, trackId, exists));
//            }
//        }
        for (Long playlistId : playlistIds) {
            if (!playlistTrackMap.containsKey(playlistId)) {
                continue;
            }
            for (Long trackId : trackIds) {
                boolean exists = playlistTrackMap.containsKey(playlistId) && playlistTrackMap.get(playlistId).contains(trackId);
                if (exists){
                    responses.add(new PlaylistTrackExistsResponse(playlistId, trackId, true));
                }
            }
        }
        return responses;
    }

}
