package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.request.AlbumRequest;
import com.xdpsx.music.dto.response.AlbumResponse;
import com.xdpsx.music.model.entity.Album;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
    Album fromRequestToEntity(AlbumRequest request);

//    @Mapping(target = "totalTracks", expression = "java(entity.getTracks().size())")
    @Mapping(target = "genre", source = "entity.genre")
    @Mapping(target = "artists", source = "entity.artists")
    AlbumResponse fromEntityToResponse(Album entity);
}
