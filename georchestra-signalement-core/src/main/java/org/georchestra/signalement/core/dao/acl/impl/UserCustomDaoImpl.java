/**
 *
 */
package org.georchestra.signalement.core.dao.acl.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.acl.UserCustomDao;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.dto.UserSearchCriteria;
import org.georchestra.signalement.core.entity.acl.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author FNI18300
 */
@Repository
public class UserCustomDaoImpl extends AbstractCustomDaoImpl implements UserCustomDao {

	private static final String FIELD_LOGIN = "login";

	private static final String FIELD_EMAIL = "email";

	@Autowired
	private EntityManager entityManager;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<UserEntity> searchUsers(UserSearchCriteria searchCriteria, Pageable pageable) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<UserEntity> countRoot = countQuery.from(UserEntity.class);

		buildQuery(searchCriteria, builder, countQuery, countRoot);

		Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

		// Si aucun résultat
		if (totalCount == 0) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}

		CriteriaQuery<UserEntity> searchQuery = builder.createQuery(UserEntity.class);
		Root<UserEntity> searchRoot = searchQuery.from(UserEntity.class);

		buildQuery(searchCriteria, builder, searchQuery, searchRoot);
		searchQuery.orderBy(QueryUtils.toOrders(pageable.getSort(), searchRoot, builder));

		TypedQuery<UserEntity> typedQuery = entityManager.createQuery(searchQuery);
		List<UserEntity> tiersEntities = typedQuery.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();
		return new PageImpl<>(tiersEntities, pageable, totalCount.intValue());

	}

	private void buildQuery(UserSearchCriteria searchCriteria, CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery,
			Root<UserEntity> root) {
		if (searchCriteria != null) {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotEmpty(searchCriteria.getLogin())) {
				if (isWildCarded(searchCriteria.getLogin())) {
					predicates.add(
							builder.like(builder.lower(root.get(FIELD_LOGIN)), wildcard(searchCriteria.getLogin())));
				} else {
					predicates.add(builder.equal(root.get(FIELD_LOGIN), searchCriteria.getLogin()));
				}
			}
			if (searchCriteria.getEmail() != null) {
				if (isWildCarded(searchCriteria.getEmail())) {
					predicates.add(
							builder.like(builder.lower(root.get(FIELD_EMAIL)), wildcard(searchCriteria.getEmail())));
				} else {
					predicates.add(builder.equal(root.get(FIELD_EMAIL), searchCriteria.getLogin()));
				}
			}
			if (CollectionUtils.isNotEmpty(predicates)) {
				criteriaQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
			}
		}
	}

	@Override
	protected Map<String, Path<?>> addJoinSortCriteria(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery,
			Root<?> root, SortCriteria sortCriteria) {
		return null;
	}

}
