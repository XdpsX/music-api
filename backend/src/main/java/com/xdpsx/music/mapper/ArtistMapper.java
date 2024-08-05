package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.request.ArtistRequest;
import com.xdpsx.music.dto.response.ArtistResponse;
import com.xdpsx.music.model.entity.Artist;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    Artist fromRequestToEntity(ArtistRequest request);
    ArtistResponse fromEntityToResponse(Artist entity);
}
