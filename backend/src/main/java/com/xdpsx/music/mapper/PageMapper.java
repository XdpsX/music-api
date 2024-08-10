package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.common.PageResponse;
import com.xdpsx.music.dto.response.*;
import com.xdpsx.music.model.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PageMapper {
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final TrackMapper trackMapper;
    private final PlaylistMapper playlistMapper;
    private final UserMapper userMapper;

    public PageResponse<ArtistResponse> toArtistPageResponse(Page<Artist> artistPage){
        return toPageResponse(artistPage, artistMapper::fromEntityToResponse);
    }
    public PageResponse<AlbumResponse> toAlbumPageResponse(Page<Album> albumPage){
        return toPageResponse(albumPage, albumMapper::fromEntityToResponse);
    }
    public PageResponse<TrackResponse> toTrackPageResponse(Page<Track> trackPage){
        return toPageResponse(trackPage, trackMapper::fromEntityToResponse);
    }
    public PageResponse<PlaylistResponse> toPlaylistPageResponse(Page<Playlist> playlistPage){
        return toPageResponse(playlistPage, playlistMapper::fromEntityToResponse);
    }
    public PageResponse<UserResponse> toUserPageResponse(Page<User> userPage){
        return toPageResponse(userPage, userMapper::fromEntityToResponse);
    }

    public <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        List<R> responses = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());
        return PageResponse.<R>builder()
                .items(responses)
                .pageNum(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
