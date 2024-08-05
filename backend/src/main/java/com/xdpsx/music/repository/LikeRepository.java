package com.xdpsx.music.repository;

import com.xdpsx.music.model.entity.Like;
import com.xdpsx.music.model.id.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, LikeId> {
}
