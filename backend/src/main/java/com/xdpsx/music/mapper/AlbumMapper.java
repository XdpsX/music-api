package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.request.AlbumRequest;
import com.xdpsx.music.dto.response.AlbumResponse;
import com.xdpsx.music.model.entity.Album;
import com.xdpsx.music.repository.TrackRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class AlbumMapper {

    @Autowired
    private TrackRepository trackRepository;

    public abstract Album fromRequestToEntity(AlbumRequest request);

    @Mapping(target = "genre", source = "entity.genre")
    @Mapping(target = "artists", source = "entity.artists")
    protected abstract AlbumResponse mapToResponse(Album entity);

    public AlbumResponse fromEntityToResponse(Album entity){
        AlbumResponse response = mapToResponse(entity);
        int totalTracks = trackRepository.countByAlbumId(entity.getId());
        response.setTotalTracks(totalTracks);
        return response;
    }


}
