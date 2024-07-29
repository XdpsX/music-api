package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.entity.Artist;
import com.xdpsx.music.entity.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtistCriteriaRepository {
    Page<Artist> findWithFilters(
            Pageable pageable,
            String name,
            String sort,
            Gender gender
    );
}
