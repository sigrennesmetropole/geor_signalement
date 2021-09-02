/**
 *
 */
package org.georchestra.signalement.core.dao.acl.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionCustomDao;
import org.georchestra.signalement.core.dto.ContextDescriptionSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author FNI18300
 */
@Repository
public class ContextDescriptionCustomDaoImpl extends AbstractCustomDaoImpl implements ContextDescriptionCustomDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContextDescriptionCustomDaoImpl.class);

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
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("{} results founded", result.size());
		}
		return result;
	}

	@Override
	public ContextDescriptionEntity getContextDescriptionByName(String name) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<ContextDescriptionEntity> searchQuery = builder.createQuery(ContextDescriptionEntity.class);
		Root<ContextDescriptionEntity> searchRoot = searchQuery.from(ContextDescriptionEntity.class);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(searchRoot.get("name"), name));

		searchQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
		TypedQuery<ContextDescriptionEntity> typedQuery = entityManager.createQuery(searchQuery);
		List<ContextDescriptionEntity> result = typedQuery.getResultList().stream()
				.distinct().sorted(Comparator.comparing(ContextDescriptionEntity::getProcessDefinitionKey))
				.collect(Collectors.toList());
		if (result.size() == 0) {
			return null;
		} else if (result.size() > 1) {
			LOGGER.warn("More than one context found ! Only return the first one, ordered by key");
		}
		return result.get(0);

	}

	@Override
	public ContextDescriptionEntity updateContextDescription(ContextDescriptionEntity updatedContext) {
		ContextDescriptionEntity toUpdate = getContextDescriptionByName(updatedContext.getName());
		toUpdate.setLabel(updatedContext.getLabel());
		toUpdate.setProcessDefinitionKey(updatedContext.getProcessDefinitionKey());
		toUpdate.setRevision(updatedContext.getRevision());
		return toUpdate;
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
