package com.xdpsx.music.repository.criteria;

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

        Predicate[] predicates = buildPredicates(cb, root, name, ownerId);
        cq.where(predicates);
        applySorting(cb, cq, root, sortField);

        List<Playlist> playlists = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        long totalRows = getTotalRows(cb, name, ownerId);
        return new PageImpl<>(playlists, pageable, totalRows);
    }

    private Predicate[] buildPredicates(CriteriaBuilder cb, Root<Playlist> playlist, String name, Long ownerId) {
        List<Predicate> predicates = new ArrayList<>();
        if (ownerId != null) {
            predicates.add(cb.equal(playlist.get("owner").get("id"), ownerId));
        }
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(cb.lower(playlist.get("name")), "%" + name.toLowerCase() + "%"));
        }
        return predicates.toArray(new Predicate[0]);
    }

    private void applySorting(CriteriaBuilder cb, CriteriaQuery<Playlist> query, Root<Playlist> playlist, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            switch (field) {
                case TOTAL_TRACKS_FIELD -> {
                    Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
                    Root<PlaylistTrack> playlistTrack = subquery.from(PlaylistTrack.class);
                    subquery.select(cb.count(playlistTrack))
                            .where(cb.equal(playlistTrack.get("playlist"), playlist));

                    query.orderBy(desc ? cb.desc(subquery) : cb.asc(subquery));
                }
                case DATE_FIELD -> {
                    Path<LocalDateTime> updatedAtPath = playlist.get("updatedAt");
                    Path<LocalDateTime> createdAtPath = playlist.get("createdAt");
                    Expression<LocalDateTime> dateExpression = cb.coalesce(updatedAtPath, createdAtPath);
                    query.orderBy(desc ? cb.desc(dateExpression) : cb.asc(dateExpression));
                }
                case NAME_FIELD -> {
                    Path<?> path = playlist.get("name");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                default -> {
                }
            }
        }
    }

    private long getTotalRows(CriteriaBuilder cb, String name, Long ownerId) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Playlist> countRoot = countQuery.from(Playlist.class);

        Predicate[] countPredicates = buildPredicates(cb, countRoot, name, ownerId);
        countQuery.select(cb.count(countRoot)).where(countPredicates);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

}
