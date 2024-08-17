package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.request.ArtistRequest;
import com.xdpsx.music.dto.response.ArtistResponse;
import com.xdpsx.music.model.entity.Artist;
import com.xdpsx.music.util.Links;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ArtistMapper {
    public abstract Artist fromRequestToEntity(ArtistRequest request);
    protected abstract ArtistResponse mapToResponse(Artist entity);

    public ArtistResponse fromEntityToResponse(Artist entity){
        ArtistResponse response = mapToResponse(entity);
        Links.addLinksToArtist(response);
        return response;
    }
}
