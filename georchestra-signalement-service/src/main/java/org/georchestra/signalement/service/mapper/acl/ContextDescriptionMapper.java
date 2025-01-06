package org.georchestra.signalement.service.mapper.acl;

import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.service.mapper.AbstractMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContextDescriptionMapper extends AbstractMapper<ContextDescriptionEntity, ContextDescription> {

	@Override
	@InheritInverseConfiguration
	ContextDescriptionEntity dtoToEntity(ContextDescription dto);

	/**
	 * Converti un dossier en DossierDto.
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	@Override
	ContextDescription entityToDto(ContextDescriptionEntity entity);

}
