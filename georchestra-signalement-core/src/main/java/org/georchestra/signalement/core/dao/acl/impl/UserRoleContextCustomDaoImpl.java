package org.georchestra.signalement.core.dao.acl.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    private static final String FIELD_LOGIN = "login";
	private static final String FIELD_NAME = "name";
	private static final String FIELD_ID = "id";
	private static final String FIELD_GEOGRAPHIC_AREA = "geographicArea";
	private static final String FIELD_CONTEXT_DESCRIPTION = "contextDescription";
	private static final String FIELD_ROLE = "role";
	private static final String FIELD_USER = "user";
	@Autowired
    EntityManager entityManager;

    @Override
    protected Map<String, Path<?>> addJoinSortCriteria(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<?> root, SortCriteria sortCriteria) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<UserRoleContextEntity> searchUserRoleContexts(UserRoleContextSearchCriteria searchCriteria, Pageable pageable) {
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
                            CriteriaQuery<?> criteriaQuery, Root<UserRoleContextEntity> root) {
        if (searchCriteria != null) {
            List<Predicate> predicates = new ArrayList<>();
            if (searchCriteria.getUserId() != null) {
                predicates.add(builder.equal(root.get(FIELD_USER).get(FIELD_ID), searchCriteria.getUserId()));
            }
            if (StringUtils.isNotEmpty(searchCriteria.getUserLogin())) {
                predicates.add(builder.equal(root.get(FIELD_USER).get(FIELD_LOGIN), searchCriteria.getUserLogin()));
            }
            if (searchCriteria.getRoleId() != null) {
                predicates.add(builder.equal(root.get(FIELD_ROLE).get(FIELD_ID), searchCriteria.getRoleId()));
            }
            if (searchCriteria.getRoleName() != null) {
                predicates.add(builder.equal(root.get(FIELD_ROLE).get(FIELD_NAME), searchCriteria.getRoleName()));
            }
            if (searchCriteria.getContextDescriptionId() != null) {
                predicates.add(builder.equal(root.get(FIELD_CONTEXT_DESCRIPTION).get(FIELD_ID), searchCriteria.getContextDescriptionId()));
            }
            if (StringUtils.isNotEmpty(searchCriteria.getContextDescriptionName())) {
                predicates.add(builder.equal(root.get(FIELD_CONTEXT_DESCRIPTION).get(FIELD_NAME), searchCriteria.getContextDescriptionName()));
            }
            if (searchCriteria.getGeographicAreaId() != null) {
                predicates.add(builder.equal(root.get(FIELD_GEOGRAPHIC_AREA).get(FIELD_ID), searchCriteria.getGeographicAreaId()));
            }
            if (CollectionUtils.isNotEmpty(predicates)) {
                criteriaQuery.where(builder.and(predicates.toArray(Predicate[]::new)));
            }
        }
    }
}
