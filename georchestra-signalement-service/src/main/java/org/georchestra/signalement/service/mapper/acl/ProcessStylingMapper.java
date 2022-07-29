package org.georchestra.signalement.service.mapper.acl;

import org.georchestra.signalement.core.dto.StyleContainer;
import org.georchestra.signalement.core.dto.ProcessStyling;
import org.georchestra.signalement.core.entity.acl.UserEntity;
import org.georchestra.signalement.core.entity.styling.ProcessStylingEntity;
import org.georchestra.signalement.core.entity.styling.StylingEntity;
import org.georchestra.signalement.service.helper.geojson.GeoJSonHelper;
import org.georchestra.signalement.service.mapper.AbstractMapper;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;

    @Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
    public interface ProcessStylingMapper extends AbstractMapper<ProcessStylingEntity, ProcessStyling> {

        GeoJSonHelper helper = new GeoJSonHelper();
        @Override
        @InheritInverseConfiguration
        ProcessStylingEntity dtoToEntity(ProcessStyling dto);

        @Override
        ProcessStyling entityToDto(ProcessStylingEntity entity);

        void dtoToEntity(ProcessStyling dto, @MappingTarget ProcessStylingEntity entity);

        @IterableMapping(qualifiedByName = "entityToDto")
        List<ProcessStyling> entitiesToDtos(Collection<ProcessStylingEntity> entities);
}
