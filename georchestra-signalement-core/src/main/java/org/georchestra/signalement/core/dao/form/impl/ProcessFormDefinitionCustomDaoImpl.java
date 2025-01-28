/**
 * 
 */
package org.georchestra.signalement.core.dao.form.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.form.ProcessFormDefinitionCustomDao;
import org.georchestra.signalement.core.dto.ProcessFormDefinitionSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.entity.form.ProcessFormDefinitionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author FNI18300
 *
 */
@Repository
public class ProcessFormDefinitionCustomDaoImpl extends AbstractCustomDaoImpl implements ProcessFormDefinitionCustomDao {

	private static final String PROCESS_DEFINITION_ID_PROPERTY = "processDefinitionId";
	private static final String USER_TASK_ID_PROPERTY = "userTaskId";
	private static final String REVISION_PROPERTY = "revision";
	@Autowired
	private EntityManager entityManager;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<ProcessFormDefinitionEntity> searchProcessFormDefintions(
			ProcessFormDefinitionSearchCriteria searchCriteria, SortCriteria sortCriteria) {
		List<ProcessFormDefinitionEntity> result = null;

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<ProcessFormDefinitionEntity> searchQuery = builder.createQuery(ProcessFormDefinitionEntity.class);
		Root<ProcessFormDefinitionEntity> searchRoot = searchQuery.from(ProcessFormDefinitionEntity.class);

		buildQuery(searchCriteria, builder, searchQuery, searchRoot);
		applySortCriteria(builder, searchQuery, searchRoot, sortCriteria);

		TypedQuery<ProcessFormDefinitionEntity> typedQuery = entityManager.createQuery(searchQuery);
		result = typedQuery.getResultList().stream().distinct().collect(Collectors.toList());

		return result;
	}

	private void buildQuery(ProcessFormDefinitionSearchCriteria searchCriteria, CriteriaBuilder builder,
			CriteriaQuery<ProcessFormDefinitionEntity> criteriaQuery, Root<ProcessFormDefinitionEntity> root) {
		if (searchCriteria != null) {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotEmpty(searchCriteria.getProcessDefinitionId())) {
				predicates.add(builder.equal(root.get(PROCESS_DEFINITION_ID_PROPERTY), searchCriteria.getProcessDefinitionId()));
			}
			if (searchCriteria.getRevision() != null || searchCriteria.isAcceptFlexRevision()) {
				buildPredicateRevision(searchCriteria, builder, root, predicates);
			}
			if (StringUtils.isNotEmpty(searchCriteria.getUserTaskId())) {
				buildPredicateUserTaskId(searchCriteria, builder, root, predicates);
			}
			if (CollectionUtils.isNotEmpty(predicates)) {
				criteriaQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
			}
		}
	}

	private void buildPredicateRevision(ProcessFormDefinitionSearchCriteria searchCriteria, CriteriaBuilder builder,
			Root<ProcessFormDefinitionEntity> root, List<Predicate> predicates) {
		Predicate p = builder.equal(root.get(REVISION_PROPERTY), searchCriteria.getRevision());
		if (!searchCriteria.isAcceptFlexRevision()) {
			predicates.add(p);
		} else {
			Predicate f = builder.isNull(root.get(REVISION_PROPERTY));
			predicates.add(builder.or(p, f));
		}
	}

	private void buildPredicateUserTaskId(ProcessFormDefinitionSearchCriteria searchCriteria, CriteriaBuilder builder,
			Root<ProcessFormDefinitionEntity> root, List<Predicate> predicates) {
		Predicate p = builder.equal(root.get(USER_TASK_ID_PROPERTY), searchCriteria.getUserTaskId());
		if (!searchCriteria.isAcceptFlexUserTaskId()) {
			predicates.add(p);
		} else {
			Predicate f = builder.isNull(root.get(USER_TASK_ID_PROPERTY));
			predicates.add(builder.or(p, f));
		}
	}

	@Override
	protected Map<String, Path<?>> addJoinSortCriteria(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery,
			Root<?> root, SortCriteria sortCriteria) {
		return null;
	}

}
