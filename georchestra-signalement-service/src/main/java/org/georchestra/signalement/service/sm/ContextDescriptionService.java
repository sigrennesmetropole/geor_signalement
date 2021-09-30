/**
 *
 */
package org.georchestra.signalement.service.sm;

import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.ContextDescriptionSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

	Page<ContextDescription> searchPageContextDescriptions(Pageable pageable, SortCriteria sortCriteria,
														   String description, String workflow);

	ContextDescription getContextDescription(String name);

	void deleteContextDescription(String name) throws InvalidDataException;

	ContextDescription updateContextDescription(ContextDescription contextDescription) throws InvalidDataException;

	ContextDescription createContextDescription(ContextDescription contextDescription) throws InvalidDataException;
}
