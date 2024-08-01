package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.entity.Artist;
import com.xdpsx.music.entity.Gender;
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
public class ArtistCriteriaRepositoryImpl implements ArtistCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Artist> findWithFilters(Pageable pageable, String name, String sort, Gender gender) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Artist> query = cb.createQuery(Artist.class);
        Root<Artist> root = query.from(Artist.class);

        // Predicates for filters
        List<Predicate> predicates = buildPredicates(cb, root, name, gender);
        query.where(predicates.toArray(new Predicate[0]));

        // Sorting
        if (sort != null) {
            query.orderBy(getSortOrder(sort, cb, root));
        }

        // Execute main query
        List<Artist> artists = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Create a new count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Artist> countRoot = countQuery.from(Artist.class); // New Root for count query

        // Predicates for the count query
        List<Predicate> countPredicates =  buildPredicates(cb, countRoot, name, gender);
        countQuery.select(cb.count(countRoot)).where(countPredicates.toArray(new Predicate[0]));

        // Execute the count query
        Long totalRows = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(artists, pageable, totalRows);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Artist> root, String name, Gender gender) {
        List<Predicate> predicates = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(root.get("name"), "%" + name + "%"));
        }
        if (gender != null) {
            predicates.add(cb.equal(root.get("gender"), gender.name()));
        }
        return predicates;
    }

    private Long countArtists(CriteriaBuilder cb, List<Predicate> predicates) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Artist> countRoot = countQuery.from(Artist.class);
        countQuery.select(cb.count(countRoot)).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private Order getSortOrder(String sortField, CriteriaBuilder cb, Root root) {
        String actualField = sortField;
        if (sortField.startsWith("-")){
            actualField = sortField.substring(1);
        }

        if (actualField.equalsIgnoreCase(NAME_FIELD)) {
            return sortField.startsWith("-") ? cb.desc(root.get(actualField)) : cb.asc(root.get(actualField));
        }else {
            Expression<LocalDateTime> expression = root.get("createdAt");
            return sortField.startsWith("-") ? cb.desc(expression) : cb.asc(expression);
        }
    }
}
