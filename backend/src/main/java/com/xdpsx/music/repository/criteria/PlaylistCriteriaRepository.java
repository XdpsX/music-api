package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.model.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlaylistCriteriaRepository {
    Page<Playlist> findAllWithFilters(Pageable pageable, String name, String sortField, Long ownerId);
}
