/**
 * 
 */
package org.georchestra.signalement.service.sm;

import java.util.List;

import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.ContextDescriptionSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;

/**
 * @author FNI18300
 *
 */
public interface ContextDescriptionService {

	/**
	 * Retourne la liste des contextes disponibles
	 * 
	 * @param searchCriteria
	 * @param sortCriteria
	 * @return
	 */
	List<ContextDescription> searchContextDescriptions(ContextDescriptionSearchCriteria searchCriteria,
			SortCriteria sortCriteria);

}
