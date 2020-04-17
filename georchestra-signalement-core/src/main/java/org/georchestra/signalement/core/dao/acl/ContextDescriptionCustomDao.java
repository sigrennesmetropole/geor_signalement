/**
 * 
 */
package org.georchestra.signalement.core.dao.acl;

import java.util.List;

import org.georchestra.signalement.core.dto.ContextDescriptionSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;

/**
 * @author FNI18300
 *
 */
public interface ContextDescriptionCustomDao {
	/**
	 * 
	 * @param searchCriteria
	 * @param sortCriteria
	 * @return
	 */
	List<ContextDescriptionEntity> searchContextDescriptions(ContextDescriptionSearchCriteria searchCriteria,
			SortCriteria sortCriteria);
}
