package com.xdpsx.music.service.impl;

import com.xdpsx.music.exception.BadRequestException;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.model.entity.Like;
import com.xdpsx.music.model.entity.Track;
import com.xdpsx.music.model.entity.User;
import com.xdpsx.music.model.id.LikeId;
import com.xdpsx.music.repository.LikeRepository;
import com.xdpsx.music.repository.TrackRepository;
import com.xdpsx.music.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final TrackRepository trackRepository;

    @Override
    public void likeTrack(Long trackId, User loggedUser) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Not found track with ID=%s", trackId)
                ));

        LikeId likeId = new LikeId();
        likeId.setUserId(loggedUser.getId());
        likeId.setTrackId(trackId);

        if (likeRepository.existsById(likeId)) {
            throw new BadRequestException(
                    String.format("Track with id=%s has already been liked", trackId)
            );
        }
        Like like = Like.builder()
                .id(likeId)
                .user(loggedUser)
                .track(track)
                .build();
        likeRepository.save(like);
    }

    @Override
    public void unlikeTrack(Long trackId, User loggedUser) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Not found track with ID=%s", trackId)
                ));
        LikeId likeId = new LikeId();
        likeId.setUserId(loggedUser.getId());
        likeId.setTrackId(trackId);

        if (!likeRepository.existsById(likeId)) {
            throw new BadRequestException(
                    String.format("Track with id=%s has not been liked", trackId)
            );
        }
        likeRepository.deleteById(likeId);
    }

    @Override
    public boolean isTrackLikedByUser(Long trackId, Long userId) {
        return likeRepository.existsById(new LikeId(userId, trackId));
    }
}
