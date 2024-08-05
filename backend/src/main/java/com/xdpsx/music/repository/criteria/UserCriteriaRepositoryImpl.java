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
    public Page<User> findAllWithFilters(Pageable pageable, String name, String sort,
                                         Boolean accountLocked, Boolean enabled) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        Predicate filtersPredicate = createFiltersPredicate(cb, root, name, accountLocked, enabled);
        query.where(filtersPredicate);
        applySorting(cb, query, root, sort);

        List<User> users = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = getTotalCount(cb, name, accountLocked, enabled);
        return new PageImpl<>(users, pageable, total);
    }

    private long getTotalCount(CriteriaBuilder cb, String name, Boolean accountLocked, Boolean enabled) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countRoot = countQuery.from(User.class);

        Predicate countPredicate = createFiltersPredicate(cb, countRoot, name, accountLocked, enabled);
        countQuery.select(cb.count(countRoot)).where(countPredicate);

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private Predicate createFiltersPredicate(CriteriaBuilder cb, Root<User> root, String name,
                                             Boolean accountLocked, Boolean enabled) {
        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (accountLocked != null) {
            predicates.add(cb.equal(root.get("accountLocked"), accountLocked));
        }
        if (enabled != null) {
            predicates.add(cb.equal(root.get("enabled"), enabled));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void applySorting(CriteriaBuilder cb, CriteriaQuery<User> cq, Root<User> root, String sortField) {
        if (sortField != null && !sortField.isEmpty()) {
            boolean desc = sortField.startsWith("-");
            String field = desc ? sortField.substring(1) : sortField;

            Path<?> path = switch (field) {
                case DATE_FIELD -> root.get("createdAt");
                case NAME_FIELD -> root.get("name");
                default -> null;
            };

            if (path != null) {
                cq.orderBy(desc ? cb.desc(path) : cb.asc(path));
            }
        }
    }
}
