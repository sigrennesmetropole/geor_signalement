/**
 *
 */
package org.georchestra.signalement.core.dao.acl;

import org.georchestra.signalement.core.dto.ContextDescriptionSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;

import java.util.List;

/**
 * @author FNI18300
 *
 */
public interface ContextDescriptionCustomDao {
	/**
	 * @param searchCriteria
	 * @param sortCriteria
	 * @return
	 */
	List<ContextDescriptionEntity> searchContextDescriptions(ContextDescriptionSearchCriteria searchCriteria,
															 SortCriteria sortCriteria);

	ContextDescriptionEntity getContextDescriptionByName(String name);

	ContextDescriptionEntity updateContextDescription(ContextDescriptionEntity updatedContext);
}
