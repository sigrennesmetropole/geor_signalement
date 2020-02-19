package org.georchestra.signalement.service.mapper;

import org.activiti.engine.repository.ProcessDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProcessDefinitionMapper {

	/**
	 * Converti une définition de processus
	 *
	 * @param entity entity to transform to dto
	 * @return DossierDto
	 */
	org.georchestra.signalement.core.dto.ProcessDefinition entityToDto(ProcessDefinition entity);
}
