/**
 * 
 */
package org.georchestra.signalement.core.dao.styling.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.styling.ProcessStylingCustomDao;
import org.georchestra.signalement.core.dao.styling.ProcessStylingDao;
import org.georchestra.signalement.core.dto.ProcessStylingSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.dto.SortCriterion;
import org.georchestra.signalement.core.entity.styling.ProcessStylingEntity;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Repository
@RequiredArgsConstructor
public class ProcessStylingCustomDaoImpl extends AbstractCustomDaoImpl implements ProcessStylingCustomDao {

	private static final String USER_TASK_ID = "userTaskId";
	private static final String REVISION = "revision";
	private static final String PROCESS_DEFINITION_ID = "processDefinitionId";


	private final EntityManager entityManager;

	private final ProcessStylingDao processStylingDao;

	@Override
	public List<ProcessStylingEntity> searchProcessStylings(ProcessStylingSearchCriteria searchCriteria,
			SortCriteria sortCriteria) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<ProcessStylingEntity> searchQuery = builder.createQuery(ProcessStylingEntity.class);
		Root<ProcessStylingEntity> searchRoot = searchQuery.from(ProcessStylingEntity.class);

		buildQuery(searchCriteria, builder, searchQuery, searchRoot, false);
		applySortCriteria(builder, searchQuery, searchRoot, sortCriteria);

		TypedQuery<ProcessStylingEntity> typedQuery = entityManager.createQuery(searchQuery);
		return typedQuery.getResultList().stream().distinct().toList();
	}

	private void buildQuery(ProcessStylingSearchCriteria searchCriteria, CriteriaBuilder builder,
			CriteriaQuery<ProcessStylingEntity> criteriaQuery, Root<ProcessStylingEntity> root, boolean flex) {
		if (searchCriteria != null) {
			List<Predicate> predicates = new ArrayList<>();
			buildProcessDefinitionIdCriteria(searchCriteria, builder, root, predicates);
			buildRevisionCriteria(searchCriteria, builder, root, flex, predicates);
			buildUserTaskIdCriteria(searchCriteria, builder, root, flex, predicates);
			if (CollectionUtils.isNotEmpty(predicates)) {
				criteriaQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
			}
		}
	}

	private void buildProcessDefinitionIdCriteria(ProcessStylingSearchCriteria searchCriteria, CriteriaBuilder builder,
			Root<ProcessStylingEntity> root, List<Predicate> predicates) {
		if (StringUtils.isNotEmpty(searchCriteria.getProcessDefinitionId())) {
			predicates.add(builder.equal(root.get(PROCESS_DEFINITION_ID), searchCriteria.getProcessDefinitionId()));
		}
	}

	private void buildUserTaskIdCriteria(ProcessStylingSearchCriteria searchCriteria, CriteriaBuilder builder,
			Root<ProcessStylingEntity> root, boolean flex, List<Predicate> predicates) {
		if (StringUtils.isNotEmpty(searchCriteria.getUserTaskId())) {
			if (flex) {
				predicates.add(builder.or(builder.equal(root.get(USER_TASK_ID), searchCriteria.getUserTaskId()),
						builder.isNull(root.get(USER_TASK_ID))));
			} else {
				predicates.add(builder.equal(root.get(USER_TASK_ID), searchCriteria.getUserTaskId()));
			}
		}
	}

	private void buildRevisionCriteria(ProcessStylingSearchCriteria searchCriteria, CriteriaBuilder builder,
			Root<ProcessStylingEntity> root, boolean flex, List<Predicate> predicates) {
		if (searchCriteria.getRevision() != null) {
			if (flex) {
				predicates.add(builder.or(builder.equal(root.get(REVISION), searchCriteria.getRevision()),
						builder.isNull(root.get(REVISION))));
			} else {
				predicates.add(builder.equal(root.get(REVISION), searchCriteria.getRevision()));
			}
		}
	}

	@Override
	protected Map<String, Path<?>> addJoinSortCriteria(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery,
			Root<?> root, SortCriteria sortCriteria) {
		return Map.of();
	}

	@Override
	public ProcessStylingEntity findBestProcessStyling(ProcessStylingSearchCriteria searchCriteria) {
		List<ProcessStylingEntity> processStylings = null;

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<ProcessStylingEntity> searchQuery = builder.createQuery(ProcessStylingEntity.class);
		Root<ProcessStylingEntity> searchRoot = searchQuery.from(ProcessStylingEntity.class);
		buildQuery(searchCriteria, builder, searchQuery, searchRoot, true);

		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addElementsItem(new SortCriterion().property(PROCESS_DEFINITION_ID).asc(true));
		sortCriteria.addElementsItem(new SortCriterion().property(REVISION).asc(true));
		sortCriteria.addElementsItem(new SortCriterion().property(USER_TASK_ID).asc(true));
		applySortCriteria(builder, searchQuery, searchRoot, sortCriteria);

		TypedQuery<ProcessStylingEntity> typedQuery = entityManager.createQuery(searchQuery);
		processStylings = typedQuery.getResultList().stream().distinct().toList();

		if (CollectionUtils.isNotEmpty(processStylings)) {
			return processStylings.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void deleteByStylingId(long id) {
		List<ProcessStylingEntity> needToBeDelete = processStylingDao.findByStylingId(id);
		for (ProcessStylingEntity processStyling : needToBeDelete){
			processStylingDao.delete(processStyling);
		}

	}

}
