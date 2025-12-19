package org.georchestra.signalement.service.mapper.acl;

import org.georchestra.signalement.core.dto.Role;
import org.georchestra.signalement.core.entity.acl.RoleEntity;
import org.georchestra.signalement.service.mapper.AbstractMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends AbstractMapper<RoleEntity, Role> {

    @Override
    @InheritInverseConfiguration
    RoleEntity dtoToEntity(Role dto);

    @Override
    Role entityToDto(RoleEntity entity);

    void dtoToEntity(Role dto, @MappingTarget RoleEntity entity);
}
