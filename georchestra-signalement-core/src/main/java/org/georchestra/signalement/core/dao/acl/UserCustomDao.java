/**
 *
 */
package org.georchestra.signalement.core.dao.acl;

import org.georchestra.signalement.core.dto.UserSearchCriteria;
import org.georchestra.signalement.core.entity.acl.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author FNI18300
 *
 */
public interface UserCustomDao {

	/**
	 * 
	 * @param searchCriteria
	 * @param pageable
	 * @return
	 */
	Page<UserEntity> searchUsers(UserSearchCriteria searchCriteria, Pageable pageable);
}
