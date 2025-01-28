package org.georchestra.signalement.service.mapper.acl;

import org.georchestra.signalement.core.dto.Role;
import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.core.entity.acl.RoleEntity;
import org.georchestra.signalement.core.entity.acl.UserEntity;
import org.georchestra.signalement.service.mapper.AbstractMapper;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends AbstractMapper<RoleEntity, Role> {

    @Override
    @InheritInverseConfiguration
    RoleEntity dtoToEntity(Role dto);

    @Override
    Role entityToDto(RoleEntity entity);

    void dtoToEntity(Role dto, @MappingTarget RoleEntity entity);
}
