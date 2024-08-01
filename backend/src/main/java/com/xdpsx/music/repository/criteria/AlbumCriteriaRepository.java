package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlbumCriteriaRepository {
    Page<Album> findAlbumsWithFilters(Pageable pageable, String name, String sortField);
    Page<Album> findAlbumsWithFilters(Pageable pageable, String name, String sortField, Long artistId, Integer genreId);
}
