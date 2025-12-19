package org.georchestra.signalement.service.mapper.acl;

import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.core.entity.acl.UserEntity;
import org.georchestra.signalement.service.mapper.AbstractMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends AbstractMapper<UserEntity, User> {

	@Override
	@InheritInverseConfiguration
	UserEntity dtoToEntity(User dto);

	@Override
	User entityToDto(UserEntity entity);
	
	void dtoToEntity(User user, @MappingTarget UserEntity entity);

}
