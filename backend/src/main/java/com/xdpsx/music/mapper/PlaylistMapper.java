package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.request.PlaylistRequest;
import com.xdpsx.music.dto.response.PlaylistResponse;
import com.xdpsx.music.model.entity.Playlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {
    Playlist fromRequestToEntity(PlaylistRequest request);

    @Mapping(target = "owner", source = "entity.owner")
    PlaylistResponse fromEntityToResponse(Playlist entity);
}
