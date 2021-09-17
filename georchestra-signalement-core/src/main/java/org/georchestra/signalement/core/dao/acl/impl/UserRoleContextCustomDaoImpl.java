package org.georchestra.signalement.core.dao.acl.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.acl.UserRoleContextCustomDao;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.dto.UserRoleContextSearchCriteria;
import org.georchestra.signalement.core.entity.acl.UserRoleContextEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class UserRoleContextCustomDaoImpl extends AbstractCustomDaoImpl implements UserRoleContextCustomDao {
    @Autowired
    EntityManager entityManager;

    @Override
    protected Map<String, Path<?>> addJoinSortCriteria(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<?> root, SortCriteria sortCriteria) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<UserRoleContextEntity> searchUserRoleContext(UserRoleContextSearchCriteria searchCriteria, Pageable pageable) {


        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<UserRoleContextEntity> countRoot = countQuery.from(UserRoleContextEntity.class);
        buildQuery(searchCriteria, builder, countQuery, countRoot);
        countQuery.select(builder.countDistinct(countRoot));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        // si aucun resultat

        if (totalCount == 0) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        CriteriaQuery<UserRoleContextEntity> searchQuery = builder.createQuery(UserRoleContextEntity.class);
        Root<UserRoleContextEntity> searchRoot = searchQuery.from(UserRoleContextEntity.class);

        buildQuery(searchCriteria, builder, searchQuery, searchRoot);
        searchQuery.select(searchRoot);
        searchQuery.orderBy(QueryUtils.toOrders(pageable.getSort(), searchRoot, builder));

        TypedQuery<UserRoleContextEntity> typedQuery = entityManager.createQuery(searchQuery);
        List<UserRoleContextEntity> results = typedQuery.setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();
        return new PageImpl<>(results, pageable, totalCount.intValue());
    }

    private void buildQuery(UserRoleContextSearchCriteria searchCriteria, CriteriaBuilder builder,
                            CriteriaQuery criteriaQuery, Root<UserRoleContextEntity> root) {
        if (searchCriteria != null) {
            List<Predicate> predicates = new ArrayList<>();
            if (searchCriteria.getUser() != null) {
                predicates.add(builder.equal(root.get("user"), searchCriteria.getUser()));
            }
            if (searchCriteria.getRole() != null) {
                predicates.add(builder.equal(root.get("role"), searchCriteria.getRole()));
            }
            if (searchCriteria.getContextDescription() != null) {
                predicates.add(builder.equal(root.get("contextDescription"), searchCriteria.getContextDescription()));
            }
            if (searchCriteria.getGeographicArea() != null) {
                predicates.add(builder.equal(root.get("geographicArea"), searchCriteria.getGeographicArea()));
            }
            if (CollectionUtils.isNotEmpty(predicates)) {
                criteriaQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
            }
        }
    }
}
