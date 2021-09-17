package org.georchestra.signalement.service.sm;

import org.georchestra.signalement.core.dto.UserRoleContext;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRoleContextService {
    Page<UserRoleContext> searchUserRoleContexts(Pageable pageable, String userLogin,
                                                 String roleName, String contextDescriptionName, Long geographicAreaId);

    void deleteUserRoleContext(Long userRoleContextId, Boolean force) throws InvalidDataException;

    UserRoleContext createUserRoleContext(UserRoleContext userRoleContext) throws InvalidDataException;

    UserRoleContext getUserRoleContext(Long longValue);
}
