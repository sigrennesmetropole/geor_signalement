package org.georchestra.signalement.service.mapper.acl;

import org.georchestra.signalement.core.dto.GeographicArea;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.georchestra.signalement.service.mapper.AbstractMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GeographicAreaMapper extends AbstractMapper<GeographicAreaEntity, GeographicArea> {
    @Override
    @InheritInverseConfiguration
    GeographicAreaEntity dtoToEntity(GeographicArea dto);

    /**
     * Converti un dossier en DossierDto.
     *
     * @param entity entity to transform to dto
     * @return DossierDto
     */
    @Override
    GeographicArea entityToDto(GeographicAreaEntity entity);

    @IterableMapping(qualifiedByName = "entityToDto")
    List<GeographicArea> entitiesToDtos(List<GeographicAreaEntity> entities);

}
