package org.georchestra.signalement.service.sm;

import org.georchestra.signalement.core.dto.UserRoleContext;
import org.georchestra.signalement.core.dto.UserRoleContextSearchCriteria;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service de recherche des user/role/context
 *
 */
public interface UserRoleContextService {
	
    Page<UserRoleContext> searchUserRoleContexts(UserRoleContextSearchCriteria searchCriteria, Pageable pageable);

    void deleteUserRoleContext(Long userRoleContextId, Boolean force) throws InvalidDataException;

    UserRoleContext createUserRoleContext(UserRoleContext userRoleContext) throws InvalidDataException;

    UserRoleContext getUserRoleContext(Long userRoleContextId);
}
