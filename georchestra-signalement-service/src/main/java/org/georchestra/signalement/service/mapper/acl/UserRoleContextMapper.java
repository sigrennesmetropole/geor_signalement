package org.georchestra.signalement.service.mapper.acl;

import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.core.dto.UserRoleContext;
import org.georchestra.signalement.core.entity.acl.UserRoleContextEntity;
import org.georchestra.signalement.service.mapper.AbstractMapper;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRoleContextMapper extends AbstractMapper<UserRoleContextEntity, UserRoleContext> {

    @Override
    @InheritInverseConfiguration
    UserRoleContextEntity dtoToEntity(UserRoleContext dto);

    @Override
    UserRoleContext entityToDto(UserRoleContextEntity entity);

    void dtoToEntity(User user, @MappingTarget UserRoleContextEntity entity);

}
