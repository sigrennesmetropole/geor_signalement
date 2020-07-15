/**
 * 
 */
package org.georchestra.signalement.core.dao.styling.impl;

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
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.styling.ProcessStylingCustomDao;
import org.georchestra.signalement.core.dto.ProcessStylingSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.dto.SortCriterion;
import org.georchestra.signalement.core.entity.styling.ProcessStylingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author FNI18300
 *
 */
@Repository
public class ProcessStylingCustomDaoImpl extends AbstractCustomDaoImpl implements ProcessStylingCustomDao {

	private static final String USER_TASK_ID = "userTaskId";
	private static final String REVISION = "revision";
	private static final String PROCESS_DEFINITION_ID = "processDefinitionId";

	@Autowired
	private EntityManager entityManager;

	@Override
	public List<ProcessStylingEntity> searchProcessStylings(ProcessStylingSearchCriteria searchCriteria,
			SortCriteria sortCriteria) {
		List<ProcessStylingEntity> result = null;

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<ProcessStylingEntity> searchQuery = builder.createQuery(ProcessStylingEntity.class);
		Root<ProcessStylingEntity> searchRoot = searchQuery.from(ProcessStylingEntity.class);

		buildQuery(searchCriteria, builder, searchQuery, searchRoot, false);
		applySortCriteria(builder, searchQuery, searchRoot, sortCriteria);

		TypedQuery<ProcessStylingEntity> typedQuery = entityManager.createQuery(searchQuery);
		result = typedQuery.getResultList().stream().distinct().collect(Collectors.toList());

		return result;
	}

	private void buildQuery(ProcessStylingSearchCriteria searchCriteria, CriteriaBuilder builder,
			CriteriaQuery<ProcessStylingEntity> criteriaQuery, Root<ProcessStylingEntity> root, boolean flex) {
		if (searchCriteria != null) {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotEmpty(searchCriteria.getProcessDefinitionId())) {
				predicates.add(builder.equal(root.get(PROCESS_DEFINITION_ID), searchCriteria.getProcessDefinitionId()));
			}
			if (searchCriteria.getRevision() != null) {
				predicates.add(builder.or(builder.equal(root.get(REVISION), searchCriteria.getRevision()),
						builder.isNull(root.get(REVISION))));
			}
			if (StringUtils.isNotEmpty(searchCriteria.getUserTaskId())) {
				predicates.add(builder.or(builder.equal(root.get(USER_TASK_ID), searchCriteria.getUserTaskId()),
						builder.isNull(root.get(USER_TASK_ID))));
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

	@Override
	public ProcessStylingEntity findBestProcessStyling(ProcessStylingSearchCriteria searchCriteria) {
		List<ProcessStylingEntity> processStylings = null;

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<ProcessStylingEntity> searchQuery = builder.createQuery(ProcessStylingEntity.class);
		Root<ProcessStylingEntity> searchRoot = searchQuery.from(ProcessStylingEntity.class);
		buildQuery(searchCriteria, builder, searchQuery, searchRoot, true);

		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addElementsItem(new SortCriterion().property(PROCESS_DEFINITION_ID));
		sortCriteria.addElementsItem(new SortCriterion().property(REVISION).asc(true));
		sortCriteria.addElementsItem(new SortCriterion().property(USER_TASK_ID).asc(true));
		applySortCriteria(builder, searchQuery, searchRoot, sortCriteria);

		TypedQuery<ProcessStylingEntity> typedQuery = entityManager.createQuery(searchQuery);
		processStylings = typedQuery.getResultList().stream().distinct().collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(processStylings)) {
			return processStylings.get(0);
		} else {
			return null;
		}
	}

}
