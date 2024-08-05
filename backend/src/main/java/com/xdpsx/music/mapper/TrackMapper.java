package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.request.TrackRequest;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.model.entity.Track;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrackMapper {
    Track fromRequestToEntity(TrackRequest request);

    @Mapping(target = "album", source = "entity.album")
    @Mapping(target = "genre", source = "entity.genre")
    @Mapping(target = "artists", source = "entity.artists")
    TrackResponse fromEntityToResponse(Track entity);
}
