/**
 * 
 */
package org.georchestra.signalement.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.dto.SortCriterion;

/**
 * @author FNI18300
 *
 */
public abstract class AbstractCustomDaoImpl {

	/**
	 * 
	 * Application des critères de tri sur la requête
	 *
	 * @param builder       le builder
	 * @param criteriaQuery la requête
	 * @param root          l'élément racine
	 * @param sortCriteria  le critère de tri
	 */
	protected void applySortCriteria(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<?> root,
			SortCriteria sortCriteria) {
		Map<String, Path<?>> paths = addJoinSortCriteria(builder, criteriaQuery, root, sortCriteria);
		if (sortCriteria != null && CollectionUtils.isNotEmpty(sortCriteria.getElements())) {
			List<Order> orders = new ArrayList<>();
			for (SortCriterion sortCriterion : sortCriteria.getElements()) {
				String property = translateSortProperty(sortCriterion.getProperty());
				Path<?> path = null;
				if (MapUtils.isNotEmpty(paths)) {
					path = paths.get(sortCriterion.getProperty());
				} else {
					path = root;
				}
				Expression<String> expression = translateExpression(property, path, builder);
				if (Boolean.TRUE.equals(sortCriterion.getAsc())) {
					orders.add(builder.asc(expression));
				} else {
					orders.add(builder.desc(expression));
				}
			}
			if (CollectionUtils.isNotEmpty(orders)) {
				criteriaQuery.orderBy(orders);
			}
		}
	}

	/**
	 *
	 * @param property le nom de la propriété
	 * @param path     le chemin de la propriété
	 * @param builder  : present car besoin dans les classes complètes
	 * @return l'expression associée à la propriété
	 */
	protected Expression<String> translateExpression(String property, Path<?> path, CriteriaBuilder builder) {
		return path.get(property);
	}

	/**
	 * Traduit les propriétés de tris en fonction des jointures
	 * 
	 *
	 * @param sortProperty la propriété de tri
	 * @return sa traduction (pour prendre en compte les "paths" des propriétés
	 */
	protected String translateSortProperty(String sortProperty) {
		return sortProperty;
	}

	/**
	 * Ajoute les jontures de tri si nécessaire
	 * 
	 *
	 * @param builder       le builder
	 * @param criteriaQuery la requête
	 * @param root          l'élément racine
	 * @param sortCriteria  le critère de tri
	 * @return la map des jointures
	 */
	protected abstract Map<String, Path<?>> addJoinSortCriteria(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery,
			Root<?> root, SortCriteria sortCriteria);

	protected boolean isWildCarded(String input) {
		return input != null && input.contains("*");
	}

	protected String wildcard(String input) {
		return input.trim().replace("*", "%").toLowerCase();
	}
}
