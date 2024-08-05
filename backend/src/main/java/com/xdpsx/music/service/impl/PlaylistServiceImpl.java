package com.xdpsx.music.service.impl;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.request.PlaylistRequest;
import com.xdpsx.music.dto.request.params.PlaylistParam;
import com.xdpsx.music.dto.response.PlaylistResponse;
import com.xdpsx.music.exception.DuplicateResourceException;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.mapper.PlaylistMapper;
import com.xdpsx.music.model.entity.Playlist;
import com.xdpsx.music.model.entity.User;
import com.xdpsx.music.repository.PlaylistRepository;
import com.xdpsx.music.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistMapper playlistMapper;
    private final PlaylistRepository playlistRepository;

    @Override
    public PlaylistResponse createPlaylist(PlaylistRequest request, User loggedUser) {
        if (playlistRepository.existsByOwnerIdAndName(loggedUser.getId(), request.getName())){
            throw new DuplicateResourceException(
                    String.format("Playlist with name=%s already exists", request.getName())
            );
        }
        Playlist playlist = playlistMapper.fromRequestToEntity(request);
        playlist.setOwner(loggedUser);

        Playlist savedPlaylist = playlistRepository.save(playlist);
        return playlistMapper.fromEntityToResponse(savedPlaylist);
    }

    @Override
    public void deletePlaylist(Long playlistId, User loggedUser) {
        Playlist playlist = playlistRepository.findByIdAndOwnerId(playlistId, loggedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Playlist with id=%s not found", playlistId)
                ));
        playlistRepository.delete(playlist);
    }

    @Override
    public PageResponse<PlaylistResponse> getAllUserPlaylists(PlaylistParam params, User loggedUser) {
        Pageable pageable = PageRequest.of(params.getPageNum() - 1, params.getPageSize());
        Page<Playlist> playlistPage = playlistRepository.findAllWithFilters(
                pageable, params.getSearch(), params.getSort(), loggedUser.getId()
        );
        List<PlaylistResponse> responses = playlistPage.getContent().stream()
                .map(playlistMapper::fromEntityToResponse)
                .collect(Collectors.toList());
        return PageResponse.<PlaylistResponse>builder()
                .items(responses)
                .pageNum(playlistPage.getNumber() + 1)
                .pageSize(playlistPage.getSize())
                .totalItems(playlistPage.getTotalElements())
                .totalPages(playlistPage.getTotalPages())
                .build();
    }
}
