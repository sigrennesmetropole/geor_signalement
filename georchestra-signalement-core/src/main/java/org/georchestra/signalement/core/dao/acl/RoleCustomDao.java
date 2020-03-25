/**
 * 
 */
package org.georchestra.signalement.core.dao.acl;

import java.util.List;

import org.georchestra.signalement.core.dto.RoleSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.entity.acl.RoleEntity;

/**
 * @author FNI18300
 *
 */
public interface RoleCustomDao {
	/**
	 * 
	 * @param searchCriteria
	 * @param sortCriteria
	 * @return
	 */
	List<RoleEntity> searchRoles(RoleSearchCriteria searchCriteria,
			SortCriteria sortCriteria);
}
