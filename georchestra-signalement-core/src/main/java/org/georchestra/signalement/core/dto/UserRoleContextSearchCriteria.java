package org.georchestra.signalement.core.dto;

import lombok.Data;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.georchestra.signalement.core.entity.acl.RoleEntity;
import org.georchestra.signalement.core.entity.acl.UserEntity;

@Data
public class UserRoleContextSearchCriteria {

    UserEntity user;
    RoleEntity role;
    ContextDescriptionEntity contextDescription;
    GeographicAreaEntity geographicArea;
}
