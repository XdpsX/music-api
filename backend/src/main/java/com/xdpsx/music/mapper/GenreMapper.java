package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.request.GenreRequest;
import com.xdpsx.music.dto.response.GenreResponse;
import com.xdpsx.music.model.entity.Genre;
import com.xdpsx.music.util.Links;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class GenreMapper {
    public abstract Genre fromRequestToEntity(GenreRequest request);
    protected abstract GenreResponse mapToResponse(Genre entity);

    public GenreResponse fromEntityToResponse(Genre entity){
        GenreResponse response = mapToResponse(entity);
        Links.addLinksToGenre(response);
        return response;
    }

}
