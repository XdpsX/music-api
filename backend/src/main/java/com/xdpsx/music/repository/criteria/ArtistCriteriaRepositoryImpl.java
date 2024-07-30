package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.entity.Artist;
import com.xdpsx.music.entity.Gender;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ArtistCriteriaRepositoryImpl implements ArtistCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Artist> findWithFilters(Pageable pageable, String name, String sort, Gender gender) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Artist> query = cb.createQuery(Artist.class);
        Root<Artist> root = query.from(Artist.class);

        List<Predicate> predicates = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(root.get("name"), "%" + name + "%"));
        }
        if (gender != null){
            predicates.add(cb.equal(root.get("gender"), gender.name()));
        }
        query.where(predicates.toArray(new Predicate[0]));

        if (sort != null){
            Order order = getSortOrder(sort, cb, root);
            query.orderBy(order);
        }

        TypedQuery<Artist> typedQuery = entityManager.createQuery(query);
        int totalRows = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Artist> artists = typedQuery.getResultList();
        return new PageImpl<>(artists, pageable, totalRows);
    }

    private Order getSortOrder(String sortField, CriteriaBuilder cb, Root root) {
        String actualField = sortField;
        if (sortField.startsWith("-")){
            actualField = sortField.substring(1);
        }

        if (actualField.equalsIgnoreCase("name")) {
            return sortField.startsWith("-") ? cb.desc(root.get(actualField)) : cb.asc(root.get(actualField));
        }else {
            Expression<LocalDateTime> expression = root.get("createdAt");
            return sortField.startsWith("-") ? cb.desc(expression) : cb.asc(expression);
        }
    }
}
