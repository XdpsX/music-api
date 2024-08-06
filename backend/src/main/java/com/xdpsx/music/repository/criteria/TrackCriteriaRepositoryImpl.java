package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.model.entity.Like;
import com.xdpsx.music.model.entity.PlaylistTrack;
import com.xdpsx.music.model.entity.Track;
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
    public Page<Track> findLikedTracksByUserId(Long userId, Pageable pageable, String name, String sort) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Track> cq = cb.createQuery(Track.class);
        Root<Track> track = cq.from(Track.class);
        Join<Track, Like> like = track.join("usersLiked");

        Predicate userPredicate = cb.equal(like.get("user").get("id"), userId);
        Predicate namePredicate = cb.conjunction();

        if (name != null && !name.isEmpty()) {
            namePredicate = cb.like(cb.lower(track.get("name")), "%" + name.toLowerCase() + "%");
        }

        cq.where(cb.and(userPredicate, namePredicate));

        applyBasicSorting(cb, cq, track, sort);

        List<Track> tracks = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = getTotalCount(cb, userId, name);

        return new PageImpl<>(tracks, pageable, total);
    }

    private long getTotalCount(CriteriaBuilder cb, Long userId, String name) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Like> countRoot = countQuery.from(Like.class);
        Join<Like, Track> track = countRoot.join("track");

        Predicate userPredicate = cb.equal(countRoot.get("user").get("id"), userId);
        Predicate namePredicate = cb.conjunction();

        if (name != null && !name.isEmpty()) {
            namePredicate = cb.like(cb.lower(track.get("name")), "%" + name.toLowerCase() + "%");
        }

        countQuery.select(cb.count(countRoot)).where(cb.and(userPredicate, namePredicate));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    @Override
    public Page<Track> findWithFilters(Pageable pageable, String name, String sort) {
        return findTracks(pageable, name, null, null, null, sort, false);
    }

    @Override
    public Page<Track> findWithAlbumFilters(Pageable pageable, String name, String sort, Long albumId) {
        return findTracksWithAlbumSorting(pageable, name, albumId, sort);
    }

    @Override
    public Page<Track> findWithGenreFilters(Pageable pageable, String name, String sort, Integer genreId) {
        return findTracks(pageable, name, null, genreId, null, sort, true);
    }

    @Override
    public Page<Track> findWithArtistFilters(Pageable pageable, String name, String sort, Long artistId) {
        return findTracks(pageable, name, null, null, artistId, sort, true);
    }

    @Override
    public Page<Track> findTracksInPlaylist(Pageable pageable, String name, String sort, Long playlistId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Track> cq = cb.createQuery(Track.class);
        Root<Track> track = cq.from(Track.class);
        Join<Track, PlaylistTrack> playlistTrack = track.join("playlists");

        Predicate playlistPredicate = cb.equal(playlistTrack.get("playlist").get("id"), playlistId);
        Predicate namePredicate = cb.conjunction();
        if (name != null && !name.isEmpty()) {
            namePredicate = cb.like(cb.lower(track.get("name")), "%" + name.toLowerCase() + "%");
        }
        cq.where(cb.and(playlistPredicate, namePredicate));

        applyBasicSorting(cb, cq, track, sort);

        List<Track> tracks = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PlaylistTrack> countRoot = countQuery.from(PlaylistTrack.class);
        Join<PlaylistTrack, Track> joinedTrack = countRoot.join("track");

        playlistPredicate = cb.equal(countRoot.get("playlist").get("id"), playlistId);
        namePredicate = cb.conjunction();
        if (name != null && !name.isEmpty()) {
            namePredicate = cb.like(cb.lower(joinedTrack.get("name")), "%" + name.toLowerCase() + "%");
        }

        countQuery.select(cb.count(countRoot)).where(cb.and(playlistPredicate, namePredicate));
        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(tracks, pageable, total);
    }

    private Page<Track> findTracks(Pageable pageable, String name, Long albumId, Integer genreId, Long artistId, String sort, boolean withAlbum) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Track> cq = cb.createQuery(Track.class);
        Root<Track> track = cq.from(Track.class);

        Predicate filtersPredicate = createFiltersPredicate(cb, track, name, albumId, genreId, artistId, withAlbum);
        cq.where(filtersPredicate);
        applyBasicSorting(cb, cq, track, sort);

        List<Track> tracks = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = getTotalCount(cb, name, albumId, genreId, artistId, withAlbum);

        return new PageImpl<>(tracks, pageable, total);
    }

    private Page<Track> findTracksWithAlbumSorting(Pageable pageable, String name, Long albumId, String sort) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Track> cq = cb.createQuery(Track.class);
        Root<Track> track = cq.from(Track.class);

        Predicate filtersPredicate = createFiltersPredicate(cb, track, name, albumId, null, null, true);
        cq.where(filtersPredicate);
        applyAlbumSorting(cb, cq, track, sort);

        List<Track> tracks = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = getTotalCount(cb, name, albumId, null, null, true);

        return new PageImpl<>(tracks, pageable, total);
    }

    private Predicate createFiltersPredicate(CriteriaBuilder cb, Root<Track> track, String name, Long albumId, Integer genreId, Long artistId, boolean withAlbum) {
        Predicate predicate = cb.conjunction();

        if (name != null && !name.isEmpty()) {
            predicate = cb.and(predicate, cb.like(cb.lower(track.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (withAlbum) {
            predicate = cb.and(predicate, albumId == null ? cb.isNull(track.get("album")) : cb.equal(track.get("album").get("id"), albumId));
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

    private void applyBasicSorting(CriteriaBuilder cb, CriteriaQuery<Track> cq, Root<Track> track, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            if (TOTAL_LIKES_FIELD.equals(field)) {
                Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
                Root<Like> like = subquery.from(Like.class);
                subquery.select(cb.count(like))
                        .where(cb.equal(like.get("track"), track));

                cq.orderBy(desc ? cb.desc(subquery) : cb.asc(subquery));
            } else {
                Path<?> path = switch (field) {
                    case DATE_FIELD -> track.get("createdAt");
                    case NAME_FIELD -> track.get("name");
                    default -> null;
                };

                if (path != null) {
                    cq.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
            }
        }
    }

    private void applyAlbumSorting(CriteriaBuilder cb, CriteriaQuery<Track> cq, Root<Track> track, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            if (TOTAL_LIKES_FIELD.equals(field)) {
                Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
                Root<Like> like = subquery.from(Like.class);
                subquery.select(cb.count(like))
                        .where(cb.equal(like.get("track"), track));

                cq.orderBy(desc ? cb.desc(subquery) : cb.asc(subquery));
            } else {
                Path<?> path = switch (field) {
                    case DATE_FIELD -> track.get("trackNumber");
                    case NAME_FIELD -> track.get("name");
                    default -> null;
                };

                if (path != null) {
                    cq.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
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
