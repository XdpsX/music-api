package com.xdpsx.music.service;

import com.xdpsx.music.model.entity.User;

public interface LikeService {
    void likeTrack(Long trackId, User loggedUser);
    void unlikeTrack(Long trackId, User loggedUser);
    boolean isTrackLikedByUser(Long trackId, Long userId);
}
