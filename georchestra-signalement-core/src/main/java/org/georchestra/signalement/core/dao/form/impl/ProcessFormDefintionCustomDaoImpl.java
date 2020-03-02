/**
 * 
 */
package org.georchestra.signalement.core.dao.form.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionCustomDao;
import org.georchestra.signalement.core.dto.ContextDescriptionSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author FNI18300
 *
 */
@Repository
public class ProcessFormDefintionCustomDaoImpl extends AbstractCustomDaoImpl implements ContextDescriptionCustomDao {

	@Autowired
	private EntityManager entityManager;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<ContextDescriptionEntity> searchContextDescriptions(ContextDescriptionSearchCriteria searchCriteria,
			SortCriteria sortCriteria) {
		List<ContextDescriptionEntity> result = null;

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<ContextDescriptionEntity> searchQuery = builder.createQuery(ContextDescriptionEntity.class);
		Root<ContextDescriptionEntity> searchRoot = searchQuery.from(ContextDescriptionEntity.class);

		buildQuery(searchCriteria, builder, searchQuery, searchRoot);
		applySortCriteria(builder, searchQuery, searchRoot, sortCriteria);

		TypedQuery<ContextDescriptionEntity> typedQuery = entityManager.createQuery(searchQuery);
		result = typedQuery.getResultList().stream().distinct().collect(Collectors.toList());

		return result;
	}

	private void buildQuery(ContextDescriptionSearchCriteria searchCriteria, CriteriaBuilder builder,
			CriteriaQuery<ContextDescriptionEntity> criteriaQuery, Root<ContextDescriptionEntity> root) {
		if (searchCriteria != null) {
			List<Predicate> predicates = new ArrayList<>();
			if (searchCriteria.getContextType() != null) {
				predicates.add(builder.equal(root.get("contextType"), searchCriteria.getContextType()));
			}
			if (searchCriteria.getGeographicType() != null) {
				predicates.add(builder.equal(root.get("geographicType"), searchCriteria.getGeographicType()));
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
