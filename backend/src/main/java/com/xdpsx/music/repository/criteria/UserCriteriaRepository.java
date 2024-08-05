package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCriteriaRepository {
    Page<User> findAllWithFilters(
            Pageable pageable,
            String name,
            String sort,
            Boolean accountLocked,
            Boolean enabled
    );
}
