package org.georchestra.signalement.service.mapper.acl;

import org.georchestra.signalement.core.dto.StyleContainer;
import org.georchestra.signalement.core.entity.styling.StylingEntity;
import org.georchestra.signalement.service.helper.geojson.GeoJSonHelper;
import org.georchestra.signalement.service.mapper.AbstractMapper;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StylingMapper extends AbstractMapper<StylingEntity, StyleContainer> {

    GeoJSonHelper helper = new GeoJSonHelper();
    @Override
    @InheritInverseConfiguration
    //@Mapping()
    StylingEntity dtoToEntity(StyleContainer dto);

    @Override
    StyleContainer entityToDto(StylingEntity entity);

    void dtoToEntity(StyleContainer dto, @MappingTarget StylingEntity entity);

}
