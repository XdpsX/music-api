package com.xdpsx.music.repository.criteria;

import com.xdpsx.music.model.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.xdpsx.music.constant.PageConstants.DATE_FIELD;
import static com.xdpsx.music.constant.PageConstants.NAME_FIELD;

@Repository
public class UserCriteriaRepositoryImpl implements UserCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<User> findWithFilters(Pageable pageable, String name, String sort,
                                         Boolean accountLocked, Boolean enabled) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        Predicate[] predicates = buildPredicates(cb, root, name, accountLocked, enabled);
        query.where(predicates);
        applySorting(cb, query, root, sort);

        List<User> users = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long totalRows = getTotalRows(cb, name, accountLocked, enabled);
        return new PageImpl<>(users, pageable, totalRows);
    }

    private Predicate[] buildPredicates(CriteriaBuilder cb, Root<User> user, String name,
                                             Boolean accountLocked, Boolean enabled) {
        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(cb.lower(user.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (accountLocked != null) {
            predicates.add(cb.equal(user.get("accountLocked"), accountLocked));
        }
        if (enabled != null) {
            predicates.add(cb.equal(user.get("enabled"), enabled));
        }

        return predicates.toArray(new Predicate[0]);
    }

    private void applySorting(CriteriaBuilder cb, CriteriaQuery<User> query, Root<User> user, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            switch (field) {
                case DATE_FIELD -> {
                    Path<?> path = user.get("createdAt");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                case NAME_FIELD -> {
                    Path<?> path = user.get("name");
                    query.orderBy(desc ? cb.desc(path) : cb.asc(path));
                }
                default -> {
                }
            }
        }
    }


    private long getTotalRows(CriteriaBuilder cb, String name, Boolean accountLocked, Boolean enabled) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countRoot = countQuery.from(User.class);

        Predicate[] countPredicates = buildPredicates(cb, countRoot, name, accountLocked, enabled);
        countQuery.select(cb.count(countRoot)).where(countPredicates);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

}
