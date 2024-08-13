package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.request.PlaylistRequest;
import com.xdpsx.music.dto.response.PlaylistResponse;
import com.xdpsx.music.model.entity.Playlist;
import com.xdpsx.music.repository.PlaylistTrackRepository;
import com.xdpsx.music.util.Links;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PlaylistMapper {
    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;

    public abstract Playlist fromRequestToEntity(PlaylistRequest request);

    @Mapping(target = "owner", source = "entity.owner")
    protected abstract PlaylistResponse mapToResponse(Playlist entity);

    public PlaylistResponse fromEntityToResponse(Playlist entity){
        PlaylistResponse response = mapToResponse(entity);
        int totalTracks = playlistTrackRepository.countByPlaylistId(entity.getId());
        response.setTotalTracks(totalTracks);
        Links.addLinksToPlaylist(response);
        return response;
    }
}
