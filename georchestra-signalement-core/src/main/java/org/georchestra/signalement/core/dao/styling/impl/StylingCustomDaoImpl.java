/**
 * 
 */
package org.georchestra.signalement.core.dao.styling.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dao.acl.impl.ContextDescriptionCustomDaoImpl;
import org.georchestra.signalement.core.dao.styling.StylingDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.styling.StylingCustomDao;
import org.georchestra.signalement.core.dto.*;
import org.georchestra.signalement.core.entity.styling.StylingEntity;
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


/**
 * @author FNI18300
 *
 */
@Repository
public class StylingCustomDaoImpl extends AbstractCustomDaoImpl implements StylingCustomDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContextDescriptionCustomDaoImpl.class);
	private static final String NAME = "name";

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private StylingDao stylingDao;



	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<StylingEntity> searchStylings(StylingSearchCriteria searchCriteria, Pageable pageable) {
		List<StylingEntity> results = null;

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		CriteriaQuery<StylingEntity> searchQuery = builder.createQuery(StylingEntity.class);
		Root<StylingEntity> searchRoot = searchQuery.from(StylingEntity.class);
		searchQuery.select(searchRoot).distinct(true)
				.orderBy(QueryUtils.toOrders(pageable.getSort(), searchRoot, builder));

		buildQuery(searchCriteria, builder, searchQuery, searchRoot);
		//applySortCriteria(builder, searchQuery, searchRoot, sortCriteria);

		TypedQuery<StylingEntity> typedQuery = entityManager.createQuery(searchQuery);
		results = typedQuery.getResultList();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("{} results founded", results.size());
		}
		return new PageImpl<>(results, pageable, results.size());
	}

	private void buildQuery(StylingSearchCriteria searchCriteria, CriteriaBuilder builder,
							CriteriaQuery<StylingEntity> criteriaQuery, Root<StylingEntity> root) {
		if (searchCriteria != null) {
			List<Predicate> predicates = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(searchCriteria.getNames())) {
				predicates.add(builder.equal(root.get(NAME), searchCriteria.getNames()));
			}
		}
	}

	@Override
	protected Map<String, Path<?>> addJoinSortCriteria(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery,
													   Root<?> root, SortCriteria sortCriteria) {
		return null;
	}
}
