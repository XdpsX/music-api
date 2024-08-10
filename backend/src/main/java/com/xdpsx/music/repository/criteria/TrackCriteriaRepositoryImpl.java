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

import java.util.ArrayList;
import java.util.List;

import static com.xdpsx.music.constant.PageConstants.*;

@Repository
public class TrackCriteriaRepositoryImpl implements TrackCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Track> findWithFilters(Pageable pageable, String name, String sort) {
        return findTracksWithFilters(pageable, name, sort, null, null);
    }

    @Override
    public Page<Track> findTracksByGenre(Pageable pageable, String name, String sort, Integer genreId) {
        return findTracksWithFilters(pageable, name, sort, genreId,null);
    }

    @Override
    public Page<Track> findTracksByArtist(Pageable pageable, String name, String sort, Long artistId) {
        return findTracksWithFilters(pageable, name, sort,null, artistId);
    }

    private Page<Track> findTracksWithFilters(Pageable pageable, String name, String sort,
                                              Integer genreId, Long artistId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Track> cq = cb.createQuery(Track.class);
        Root<Track> track = cq.from(Track.class);

        Predicate[] filterPredicates = buildPredicates(cb, track, name, genreId, artistId);
        cq.where(filterPredicates);
        applyBasicSorting(cb, cq, track, sort);

        List<Track> tracks = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long totalRows = getTotalRows(cb, name, genreId, artistId);

        return new PageImpl<>(tracks, pageable, totalRows);
    }

    private Predicate[] buildPredicates(CriteriaBuilder cb, Root<Track> track,
                                        String name, Integer genreId, Long artistId) {
        List<Predicate> predicates = new ArrayList<>();

        if (genreId != null) {
            predicates.add(cb.equal(track.get("genre").get("id"), genreId));
        }
        if (artistId != null) {
            Join<Track, ?> artists = track.join("artists");
            predicates.add(cb.equal(artists.get("id"), artistId));
        }
        if (genreId != null || artistId != null){
            predicates.add(cb.isNull(track.get("album")));
        }
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(cb.lower(track.get("name")), "%" + name.toLowerCase() + "%"));
        }

        return predicates.toArray(new Predicate[0]);
    }

    private void applyBasicSorting(CriteriaBuilder cb, CriteriaQuery<Track> query, Root<Track> track, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            switch (field) {
                case TOTAL_LIKES_FIELD -> sortByTotalLikes(cb, query, track, desc);
                case DATE_FIELD -> {
                    Path<?> path = track.get("createdAt");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                case NAME_FIELD -> {
                    Path<?> path = track.get("name");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                default -> {
                }
            }
        }
    }

    private void sortByTotalLikes(CriteriaBuilder cb, CriteriaQuery<Track> query, Root<Track> track, boolean desc) {
        Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
        Root<Like> like = subquery.from(Like.class);
        subquery.select(cb.count(like))
                .where(cb.equal(like.get("track"), track));

        query.orderBy(desc ? cb.desc(subquery) : cb.asc(subquery));
    }

    private long getTotalRows(CriteriaBuilder cb, String name, Integer genreId, Long artistId) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Track> countRoot = countQuery.from(Track.class);

        Predicate[] countPredicates = buildPredicates(cb, countRoot, name, genreId, artistId);
        countQuery.select(cb.count(countRoot)).where(countPredicates);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    @Override
    public Page<Track> findTracksByAlbum(Pageable pageable, String name, String sort, Long albumId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Track> cq = cb.createQuery(Track.class);
        Root<Track> track = cq.from(Track.class);

        Predicate[] filtersPredicates = buildPredicates(cb, track, name, albumId);
        cq.where(filtersPredicates);
        applySortingAlbumTracks(cb, cq, track, sort);

        List<Track> tracks = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = getTotalRows(cb, name, albumId);

        return new PageImpl<>(tracks, pageable, total);
    }

    private Predicate[] buildPredicates(CriteriaBuilder cb, Root<Track> track, String name, Long albumId) {
        List<Predicate> predicates = new ArrayList<>();
        if (albumId == null) {
            predicates.add(cb.isNull(track.get("album")));
        }else {
            predicates.add(cb.equal(track.get("album").get("id"), albumId));
        }
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(cb.lower(track.get("name")), "%" + name.toLowerCase() + "%"));
        }
        return predicates.toArray(new Predicate[0]);
    }

    private void applySortingAlbumTracks(CriteriaBuilder cb, CriteriaQuery<Track> query, Root<Track> track, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            switch (field) {
                case TOTAL_LIKES_FIELD -> sortByTotalLikes(cb, query, track, desc);
                case DATE_FIELD -> {
                    Path<?> path = track.get("trackNumber");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                case NAME_FIELD -> {
                    Path<?> path = track.get("name");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                default -> {
                }
            }
        }
    }

    private long getTotalRows(CriteriaBuilder cb, String name, Long albumId) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Track> countRoot = countQuery.from(Track.class);

        Predicate[] countPredicates = buildPredicates(cb, countRoot, name, albumId);
        countQuery.select(cb.count(countRoot)).where(countPredicates);

        return entityManager.createQuery(countQuery).getSingleResult();
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

        applySortingPlaylistTracks(cb, cq, track, sort);

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

    private void applySortingPlaylistTracks(CriteriaBuilder cb, CriteriaQuery<Track> query, Root<Track> track, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            switch (field) {
                case TOTAL_LIKES_FIELD -> sortByTotalLikes(cb, query, track, desc);
                case DATE_FIELD -> {
                    Join<Track, PlaylistTrack> playlistTrack = track.join("playlists");
                    Path<?> path = playlistTrack.get("trackNumber");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                case NAME_FIELD -> {
                    Path<?> path = track.get("name");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                default -> {
                }
            }
        }
    }

    @Override
    public Page<Track> findFavoriteTracksByUserId(Pageable pageable, String name, String sort, Long userId) {
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

        applySortingFavoriteTracks(cb, cq, track, sort);

        List<Track> tracks = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long totalRows = getTotalRows(cb, userId, name);

        return new PageImpl<>(tracks, pageable, totalRows);
    }

    private void applySortingFavoriteTracks(CriteriaBuilder cb, CriteriaQuery<Track> query, Root<Track> track, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            switch (field) {
                case TOTAL_LIKES_FIELD -> sortByTotalLikes(cb, query, track, desc);
                case DATE_FIELD -> {
                    Join<Track, Like> like = track.join("usersLiked");
                    Path<?> path = like.get("createdAt");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                case NAME_FIELD -> {
                    Path<?> path = track.get("name");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                default -> {
                }
            }
        }
    }

    private long getTotalRows(CriteriaBuilder cb, Long userId, String name) {
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

}
