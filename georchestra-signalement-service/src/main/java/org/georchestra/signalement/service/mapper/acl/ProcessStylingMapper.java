package org.georchestra.signalement.service.mapper.acl;

import org.georchestra.signalement.core.dto.ProcessStyling;
import org.georchestra.signalement.core.entity.styling.ProcessStylingEntity;
import org.georchestra.signalement.service.mapper.AbstractMapper;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProcessStylingMapper extends AbstractMapper<ProcessStylingEntity, ProcessStyling> {

        @Override
        @InheritInverseConfiguration
        ProcessStylingEntity dtoToEntity(ProcessStyling dto);

        @Override
        ProcessStyling entityToDto(ProcessStylingEntity entity);

        void dtoToEntity(ProcessStyling dto, @MappingTarget ProcessStylingEntity entity);
}
