package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.model.entity.Album;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.xdpsx.music.constant.PageConstants.*;

@Repository
public class AlbumCriteriaRepositoryImpl implements AlbumCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Album> findAlbumsWithFilters(Pageable pageable, String name, String sortField) {
        return findAlbumsWithFilters(pageable, name, sortField, null, null);
    }

    @Override
    public Page<Album> findAlbumsWithGenreFilters(Pageable pageable, String name, String sortField, Integer genreId) {
        return findAlbumsWithFilters(pageable, name, sortField, null, genreId);
    }

    @Override
    public Page<Album> findAlbumsWithArtistFilters(Pageable pageable, String name, String sortField, Long artistId) {
        return findAlbumsWithFilters(pageable, name, sortField, artistId, null);
    }

    @Override
    public Page<Album> findAlbumsWithFilters(Pageable pageable, String name, String sortField, Long artistId, Integer genreId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Main query
        CriteriaQuery<Album> cq = cb.createQuery(Album.class);
        Root<Album> album = cq.from(Album.class);

        Predicate predicate = createFiltersPredicate(cb, album, name, artistId, genreId);
        cq.where(predicate);
        applySorting(cb, cq, album, sortField); // Pass cb to applySorting

        // Fetch the results with pagination
        List<Album> albums = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Set totalTracks for each album
//        for (Album a : albums) {
//            // Create a new CriteriaQuery for counting tracks
//            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
//            Root<Track> trackRoot = countQuery.from(Track.class);
//
//            // Set the count selection
//            countQuery.select(cb.count(trackRoot));
//
//            // Set the where clause to filter tracks by album ID
//            countQuery.where(cb.equal(trackRoot.get("album").get("id"), a.getId()));
//
//            // Execute the count query
//            Long trackCount = entityManager.createQuery(countQuery).getSingleResult();
//            a.setTotalTracks(trackCount.intValue());
//        }

        // Count query for total elements
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Album> countRoot = countQuery.from(Album.class);
        Predicate countPredicate = createFiltersPredicate(cb, countRoot, name, artistId, genreId);
        countQuery.select(cb.count(countRoot)).where(countPredicate);
        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(albums, pageable, total);
    }

    private Predicate createFiltersPredicate(CriteriaBuilder cb, Root<Album> album, String name, Long artistId, Integer genreId) {
        Predicate predicate = cb.conjunction();
        if (name != null && !name.isEmpty()) {
            predicate = cb.and(predicate, cb.like(cb.lower(album.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (artistId != null) {
            Join<Album, ?> artists = album.join("artists");
            predicate = cb.and(predicate, cb.equal(artists.get("id"), artistId));
        }
        if (genreId != null) {
            predicate = cb.and(predicate, cb.equal(album.get("genre").get("id"), genreId));
        }
        return predicate;
    }

    private void applySorting(CriteriaBuilder cb, CriteriaQuery<Album> cq, Root<Album> album, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            switch (field) {
                case NUM_TRACKS_FIELD: {
                    Join<Album, Object> tracks = album.join("tracks", JoinType.LEFT);
                    cq.groupBy(album.get("id"));
                    cq.orderBy(desc ? cb.desc(cb.count(tracks)) : cb.asc(cb.count(tracks)));
                    break;
                }
                case DATE_FIELD: {
                    Path<?> path = album.get("releaseDate");
                    cq.orderBy(desc ? cb.desc(path) : cb.asc(path));
                    break;
                }
                case NAME_FIELD: {
                    Path<?> path = album.get("name");
                    cq.orderBy(desc ? cb.desc(path) : cb.asc(path));
                    break;
                }
                default:
                    break;
            }
        }
    }
}
