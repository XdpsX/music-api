package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.request.GenreRequest;
import com.xdpsx.music.dto.response.GenreResponse;
import com.xdpsx.music.model.entity.Genre;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    Genre fromRequestToEntity(GenreRequest request);
    GenreResponse fromEntityToResponse(Genre entity);
}
