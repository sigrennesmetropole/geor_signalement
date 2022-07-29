/**
 *
 */
package org.georchestra.signalement.core.dao.acl.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionCustomDao;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
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
import java.util.List;
import java.util.Map;

/**
 * @author FNI18300
 */
@Repository
public class ContextDescriptionCustomDaoImpl extends AbstractCustomDaoImpl implements ContextDescriptionCustomDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContextDescriptionCustomDaoImpl.class);

	private static final String FIELD_CONTEXT_TYPE = "contextType";
	private static final String FIELD_GEOGRAPHIC = "geographicType";
	private static final String FIELD_LABEL = "label";
	//private static final String FIELD_PROCESS_DEFINITIONKEY = "processDefinitionKey";
	private static final String FIELD_PROCESS_DEFINITIONKEY = "processDefinitionKey";


	@Autowired
	private EntityManager entityManager;

	@Autowired
	private ContextDescriptionDao contextDescriptionDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<ContextDescriptionEntity> searchContextDescriptions(ContextDescriptionSearchCriteria searchCriteria,
																	SortCriteria sortCriteria) {
		List<ContextDescriptionEntity> result;

		System.out.println(searchCriteria);
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<ContextDescriptionEntity> searchQuery = builder.createQuery(ContextDescriptionEntity.class);
		Root<ContextDescriptionEntity> searchRoot = searchQuery.from(ContextDescriptionEntity.class);

		buildQuery(searchCriteria, builder, searchQuery, searchRoot);
		applySortCriteria(builder, searchQuery, searchRoot, sortCriteria);

		TypedQuery<ContextDescriptionEntity> typedQuery = entityManager.createQuery(searchQuery);
		result = typedQuery.getResultList();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("{} results founded", result.size());
		}
		return result;
	}
	@Override
	public ContextDescriptionEntity updateContextDescription(ContextDescriptionEntity updatedContext) {
		ContextDescriptionEntity toUpdate = contextDescriptionDao.findByName(updatedContext.getName());
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
				predicates.add(builder.equal(root.get(FIELD_CONTEXT_TYPE), searchCriteria.getContextType()));
			}
			if (searchCriteria.getGeographicType() != null) {
				predicates.add(builder.equal(root.get(FIELD_GEOGRAPHIC), searchCriteria.getGeographicType()));
			}
			if (searchCriteria.getGeographicType() != null) {
				predicates.add(builder.equal(root.get(FIELD_GEOGRAPHIC), searchCriteria.getGeographicType()));
			}
			if (StringUtils.isNotEmpty(searchCriteria.getDescription())) {
				if (isWildCarded(searchCriteria.getDescription())) {
					predicates.add(
							builder.like(builder.lower(root.get(FIELD_LABEL)), wildcard(searchCriteria.getDescription())));
				} else {
					predicates.add(builder.equal(root.get(FIELD_LABEL), searchCriteria.getDescription()));
				}
			}
			if (StringUtils.isNotEmpty(searchCriteria.getProcessDefinitionKey())) {
				if (isWildCarded(searchCriteria.getProcessDefinitionKey())) {
					predicates.add(
							builder.like(builder.lower(root.get(FIELD_PROCESS_DEFINITIONKEY)), wildcard(searchCriteria.getProcessDefinitionKey())));
				} else {
					predicates.add(builder.equal(root.get(FIELD_PROCESS_DEFINITIONKEY), searchCriteria.getProcessDefinitionKey()));
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
