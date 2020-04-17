/**
 * 
 */
package org.georchestra.signalement.core.dao.acl.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.acl.RoleCustomDao;
import org.georchestra.signalement.core.dto.RoleSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.acl.RoleEntity;
import org.georchestra.signalement.core.entity.acl.UserEntity;
import org.georchestra.signalement.core.entity.acl.UserRoleContextEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author FNI18300
 *
 */
@Repository
public class RoleCustomDaoImpl extends AbstractCustomDaoImpl implements RoleCustomDao {

	private static final String CONTEXT_DESCRIPTION_PATH = "contextDescription";
	private static final String LOGIN_PATH = "login";
	private static final String NAME_PATH = "name";
	private static final String ROLE_PATH = "role";
	private static final String ID_PATH = "id";
	@Autowired
	private EntityManager entityManager;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<RoleEntity> searchRoles(RoleSearchCriteria searchCriteria, SortCriteria sortCriteria) {
		List<RoleEntity> result = null;

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<RoleEntity> searchQuery = builder.createQuery(RoleEntity.class);
		Root<RoleEntity> searchRoot = searchQuery.from(RoleEntity.class);

		buildQuery(searchCriteria, builder, searchQuery, searchRoot);
		applySortCriteria(builder, searchQuery, searchRoot, sortCriteria);

		TypedQuery<RoleEntity> typedQuery = entityManager.createQuery(searchQuery);
		result = typedQuery.getResultList().stream().distinct().collect(Collectors.toList());

		return result;
	}

	private void buildQuery(RoleSearchCriteria searchCriteria, CriteriaBuilder builder,
			CriteriaQuery<RoleEntity> criteriaQuery, Root<RoleEntity> root) {
		if (searchCriteria != null) {
			List<Predicate> predicates = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(searchCriteria.getUserNames())) {
				Subquery<Long> sq = criteriaQuery.subquery(Long.class);
				Root<UserEntity> rootUser = sq.from(UserEntity.class);
				Join<UserRoleContextEntity, RoleEntity> joinRole = rootUser.joinSet("userRoles").join(ROLE_PATH);
				sq.select(joinRole.get(ID_PATH))
						.where(builder.in(rootUser.get(LOGIN_PATH)).value(searchCriteria.getUserNames()));

				predicates.add(builder.in(root.get(ID_PATH)).value(sq));
			}
			if (CollectionUtils.isNotEmpty(searchCriteria.getContextDescriptionIds())) {
				Subquery<Long> sq = criteriaQuery.subquery(Long.class);
				Root<UserRoleContextEntity> rootUserRole = sq.from(UserRoleContextEntity.class);
				Join<UserRoleContextEntity, RoleEntity> joinRole = rootUserRole.join(ROLE_PATH);
				Join<UserRoleContextEntity, ContextDescriptionEntity> joinContext = rootUserRole
						.join(CONTEXT_DESCRIPTION_PATH);
				sq.select(joinRole.get(ID_PATH))
						.where(builder.in(joinContext.get(ID_PATH)).value(searchCriteria.getContextDescriptionIds()));

				predicates.add(builder.in(root.get(ID_PATH)).value(sq));
			}
			if (CollectionUtils.isNotEmpty(searchCriteria.getContextDescriptionNames())) {
				Subquery<Long> sq = criteriaQuery.subquery(Long.class);
				Root<UserRoleContextEntity> rootUserRole = sq.from(UserRoleContextEntity.class);
				Join<UserRoleContextEntity, RoleEntity> joinRole = rootUserRole.join(ROLE_PATH);
				Join<UserRoleContextEntity, ContextDescriptionEntity> joinContext = rootUserRole
						.join(CONTEXT_DESCRIPTION_PATH);
				sq.select(joinRole.get(ID_PATH))
						.where(builder.in(joinContext.get(NAME_PATH)).value(searchCriteria.getContextDescriptionNames()));

				predicates.add(builder.in(root.get(ID_PATH)).value(sq));
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
