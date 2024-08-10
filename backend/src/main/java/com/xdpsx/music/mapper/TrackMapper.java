package com.xdpsx.music.mapper;

import com.xdpsx.music.dto.request.TrackRequest;
import com.xdpsx.music.dto.response.TrackResponse;
import com.xdpsx.music.model.entity.Track;
import com.xdpsx.music.repository.LikeRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TrackMapper {
    @Autowired
    private LikeRepository likeRepository;

    public abstract Track fromRequestToEntity(TrackRequest request);

    @Mapping(target = "album", source = "entity.album")
    @Mapping(target = "genre", source = "entity.genre")
    @Mapping(target = "artists", source = "entity.artists")
    protected abstract TrackResponse mapToResponse(Track entity);

    public TrackResponse fromEntityToResponse(Track entity){
        TrackResponse response = mapToResponse(entity);
        long totalLikes = likeRepository.countByTrackId(entity.getId());
        response.setTotalLikes(totalLikes);
        return response;
    }
}
