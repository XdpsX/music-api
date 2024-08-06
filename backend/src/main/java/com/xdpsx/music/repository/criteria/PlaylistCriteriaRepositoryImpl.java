package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.model.entity.Like;
import com.xdpsx.music.model.entity.Playlist;
import com.xdpsx.music.model.entity.PlaylistTrack;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.xdpsx.music.constant.PageConstants.*;

@Repository
public class PlaylistCriteriaRepositoryImpl implements PlaylistCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Playlist> findAllWithFilters(Pageable pageable, String name, String sortField, Long ownerId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Playlist> cq = cb.createQuery(Playlist.class);
        Root<Playlist> root = cq.from(Playlist.class);

        Predicate predicate = createFiltersPredicate(cb, root, name, ownerId);
        cq.where(predicate);
        applySorting(cb, cq, root, sortField);

        List<Playlist> playlists = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        long total = getTotalCount(cb, name, ownerId);
        return new PageImpl<>(playlists, pageable, total);
    }

    private Predicate createFiltersPredicate(CriteriaBuilder cb, Root<Playlist> root, String name, Long ownerId) {
        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (ownerId != null) {
            predicates.add(cb.equal(root.get("owner").get("id"), ownerId));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void applySorting(CriteriaBuilder cb, CriteriaQuery<Playlist> cq, Root<Playlist> root, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            switch (field) {
                case TOTAL_TRACKS_FIELD -> {
                    Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
                    Root<PlaylistTrack> playlistTrackRoot = subquery.from(PlaylistTrack.class);
                    subquery.select(cb.count(playlistTrackRoot))
                            .where(cb.equal(playlistTrackRoot.get("playlist"), root));

                    cq.orderBy(desc ? cb.desc(subquery) : cb.asc(subquery));
                }
                case DATE_FIELD -> {
                    Path<LocalDateTime> updatedAtPath = root.get("updatedAt");
                    Path<LocalDateTime> createdAtPath = root.get("createdAt");
                    Expression<LocalDateTime> dateExpression = cb.coalesce(updatedAtPath, createdAtPath);
                    cq.orderBy(desc ? cb.desc(dateExpression) : cb.asc(dateExpression));
                }
                case NAME_FIELD -> {
                    Path<?> path = root.get("name");
                    cq.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                default -> {
                }
            }
        }
    }

    private long getTotalCount(CriteriaBuilder cb, String name, Long ownerId) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Playlist> countRoot = countQuery.from(Playlist.class);

        Predicate countPredicate = createFiltersPredicate(cb, countRoot, name, ownerId);
        countQuery.select(cb.count(countRoot)).where(countPredicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

}
