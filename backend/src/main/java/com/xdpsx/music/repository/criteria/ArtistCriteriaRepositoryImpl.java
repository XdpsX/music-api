package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.model.entity.Artist;
import com.xdpsx.music.model.enums.Gender;
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
public class ArtistCriteriaRepositoryImpl implements ArtistCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Artist> findWithFilters(Pageable pageable, String name, String sort, Gender gender) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Main query
        CriteriaQuery<Artist> query = cb.createQuery(Artist.class);
        Root<Artist> root = query.from(Artist.class);

        Predicate[] predicates = buildPredicates(cb, root, name, gender);
        query.where(predicates);

        applySorting(cb, query, root, sort);

        List<Artist> artists = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Count query
        Long totalRows = getTotalRows(name, gender, cb);
        return new PageImpl<>(artists, pageable, totalRows);
    }

    private Predicate[] buildPredicates(CriteriaBuilder cb, Root<Artist> artist, String name, Gender gender) {
        List<Predicate> predicates = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(artist.get("name"), "%" + name + "%"));
        }
        if (gender != null) {
            predicates.add(cb.equal(artist.get("gender"), gender.name()));
        }
        return predicates.toArray(new Predicate[0]);
    }

    private void applySorting(CriteriaBuilder cb, CriteriaQuery<Artist> query, Root<Artist> artist, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            switch (field) {
                case DATE_FIELD -> {
                    Path<?> path = artist.get("createdAt");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                case NAME_FIELD -> {
                    Path<?> path = artist.get("name");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                default -> {
                }
            }
        }
    }

    private Long getTotalRows(String name, Gender gender, CriteriaBuilder cb) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Artist> countRoot = countQuery.from(Artist.class);
        Predicate[] countPredicates =  buildPredicates(cb, countRoot, name, gender);
        countQuery.select(cb.count(countRoot)).where(countPredicates);

        Long totalRows = entityManager.createQuery(countQuery).getSingleResult();
        return totalRows;
    }

}
