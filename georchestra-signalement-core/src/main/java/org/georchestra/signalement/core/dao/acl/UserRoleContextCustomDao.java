package org.georchestra.signalement.core.dao.acl;

import org.georchestra.signalement.core.dto.UserRoleContextSearchCriteria;
import org.georchestra.signalement.core.entity.acl.UserRoleContextEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom dao pour les userRoleContext
 *
 */
public interface UserRoleContextCustomDao {

    Page<UserRoleContextEntity> searchUserRoleContexts(UserRoleContextSearchCriteria searchCriteria,
                                                      Pageable pageable);
}
