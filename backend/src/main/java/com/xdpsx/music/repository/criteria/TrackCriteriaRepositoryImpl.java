package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.entity.Track;
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
public class TrackCriteriaRepositoryImpl implements TrackCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Track> findWithFilters(Pageable pageable, String name, String sort){
        return findAllWithFilters(pageable, name, sort, null, null, null, false);
    }

    public Page<Track> findWithAlbumFilters(Pageable pageable, String name, String sort, Long albumId){
        return findAllWithFilters(pageable, name, sort, albumId,null, null, true);
    }

    public Page<Track> findWithGenreFilters(Pageable pageable, String name, String sort, Integer genreId){
        return findAllWithFilters(pageable, name, sort,null, genreId, null, true);
    }

    public Page<Track> findWithArtistFilters(Pageable pageable, String name, String sort, Long artistId){
        return findAllWithFilters(pageable, name, sort,null, null, artistId, true);
    }

    private Page<Track> findAllWithFilters(Pageable pageable, String name, String sort,
                                       Long albumId, Integer genreId, Long artistId, boolean withAlbum) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Track> cq = cb.createQuery(Track.class);
        Root<Track> track = cq.from(Track.class);

        Predicate filtersPredicate = createFiltersPredicate(cb, track, name, albumId, genreId, artistId, withAlbum);
        cq.where(filtersPredicate);
        applySorting(cb, cq, track, sort);

        List<Track> tracks = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = getTotalCount(cb, name, albumId, genreId, artistId, withAlbum);

        return new PageImpl<>(tracks, pageable, total);
    }

    private Predicate createFiltersPredicate(CriteriaBuilder cb, Root<Track> track, String name,
                                             Long albumId, Integer genreId, Long artistId, boolean withAlbum) {
        Predicate predicate = cb.conjunction();

        if (name != null && !name.isEmpty()) {
            predicate = cb.and(predicate, cb.like(cb.lower(track.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (withAlbum){
            if (albumId == null) {
                predicate = cb.and(cb.isNull(track.get("album")));
            } else {
                predicate = cb.and(predicate, cb.equal(track.get("album").get("id"), albumId));
            }
        }
        if (genreId != null) {
            predicate = cb.and(predicate, cb.equal(track.get("genre").get("id"), genreId));
        }
        if (artistId != null) {
            Join<Track, ?> artists = track.join("artists");
            predicate = cb.and(predicate, cb.equal(artists.get("id"), artistId));
        }
        return predicate;
    }

    private void applySorting(CriteriaBuilder cb, CriteriaQuery<Track> cq, Root<Track> track, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            switch (field) {
                case DATE_FIELD: {
                    Path<?> path = track.get("createdAt");
                    cq.orderBy(desc ? cb.desc(path) : cb.asc(path));
                    break;
                }
                case NAME_FIELD: {
                    Path<?> path = track.get("name");
                    cq.orderBy(desc ? cb.desc(path) : cb.asc(path));
                    break;
                }
                default:
                    break;
            }
        }
    }

    private long getTotalCount(CriteriaBuilder cb, String name, Long albumId, Integer genreId, Long artistId, boolean withAlbum) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Track> countRoot = countQuery.from(Track.class);

        Predicate countPredicate = createFiltersPredicate(cb, countRoot, name, albumId, genreId, artistId, withAlbum);
        countQuery.select(cb.count(countRoot)).where(countPredicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

}
