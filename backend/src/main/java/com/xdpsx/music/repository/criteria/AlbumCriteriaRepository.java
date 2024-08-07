package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.model.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlbumCriteriaRepository {
    Page<Album> findWithFilters(Pageable pageable, String name, String sortField);
    Page<Album> findAlbumsByGenre(Pageable pageable, String name, String sortField, Integer genreId);
    Page<Album> findAlbumsByArtist(Pageable pageable, String name, String sortField, Long artistId);
}
