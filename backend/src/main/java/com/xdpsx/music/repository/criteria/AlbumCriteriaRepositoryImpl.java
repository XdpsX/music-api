package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.model.entity.Album;
import com.xdpsx.music.model.entity.Artist;
import com.xdpsx.music.model.entity.Track;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.xdpsx.music.constant.PageConstants.*;

@Repository
public class AlbumCriteriaRepositoryImpl implements AlbumCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Album> findWithFilters(Pageable pageable, String name, String sortField) {
        return findAlbumsWithFilters(pageable, name, sortField, null, null);
    }

    @Override
    public Page<Album> findAlbumsByGenre(Pageable pageable, String name, String sortField, Integer genreId) {
        return findAlbumsWithFilters(pageable, name, sortField, null, genreId);
    }

    @Override
    public Page<Album> findAlbumsByArtist(Pageable pageable, String name, String sortField, Long artistId) {
        return findAlbumsWithFilters(pageable, name, sortField, artistId, null);
    }

    private Page<Album> findAlbumsWithFilters(Pageable pageable, String name, String sortField, Long artistId, Integer genreId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Main query
        CriteriaQuery<Album> cq = cb.createQuery(Album.class);
        Root<Album> root = cq.from(Album.class);

        Predicate[] predicates = buildPredicates(cb, root, name, artistId, genreId);
        cq.where(predicates);
        applySorting(cb, cq, root, sortField);

        // Fetch the results with pagination
        List<Album> albums = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Count query for total elements
        long totalRows = getTotalRows(name, artistId, genreId, cb);
        return new PageImpl<>(albums, pageable, totalRows);
    }

    private Predicate[] buildPredicates(CriteriaBuilder cb, Root<Album> album,
                                            String name, Long artistId, Integer genreId) {
        List<Predicate> predicates = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(cb.lower(album.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (artistId != null) {
            Join<Album, Artist> artists = album.join("artists");
            predicates.add(cb.equal(artists.get("id"), artistId));
        }
        if (genreId != null) {
            predicates.add(cb.equal(album.get("genre").get("id"), genreId));
        }
        return predicates.toArray(new Predicate[0]);
    }

    private void applySorting(CriteriaBuilder cb, CriteriaQuery<Album> query, Root<Album> album, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            switch (field) {
                case TOTAL_TRACKS_FIELD -> {
                    Join<Album, Track> tracks = album.join("tracks", JoinType.LEFT);
                    query.groupBy(album.get("id"));
                    query.orderBy(desc ? cb.desc(cb.count(tracks)) : cb.asc(cb.count(tracks)));
                }
                case DATE_FIELD -> {
                    Path<?> path = album.get("releaseDate");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                case NAME_FIELD -> {
                    Path<?> path = album.get("name");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                default -> {
                }
            }
        }
    }

    private long getTotalRows(String name, Long artistId, Integer genreId, CriteriaBuilder cb) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Album> countRoot = countQuery.from(Album.class);
        Predicate[] countPredicates = buildPredicates(cb, countRoot, name, artistId, genreId);
        countQuery.select(cb.count(countRoot)).where(countPredicates);

        long totalRows = entityManager.createQuery(countQuery).getSingleResult();
        return totalRows;
    }
}
