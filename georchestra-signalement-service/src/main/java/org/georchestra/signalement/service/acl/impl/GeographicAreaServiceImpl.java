package org.georchestra.signalement.service.acl.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.acl.GeographicAreaCustomDao;
import org.georchestra.signalement.core.dao.acl.GeographicAreaDao;
import org.georchestra.signalement.core.dto.GeographicArea;
import org.georchestra.signalement.core.dto.GeographicAreaSearchCriteria;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.georchestra.signalement.service.acl.GeographicAreaService;
import org.georchestra.signalement.service.common.ErrorMessageConstants;
import org.georchestra.signalement.service.mapper.acl.GeographicAreaMapper;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GeographicAreaServiceImpl implements GeographicAreaService {

    @Autowired
    private GeographicAreaCustomDao geographicAreaCustumDao;

    @Autowired
    private GeographicAreaMapper geographicAreaMapper;

    @Autowired
    GeographicAreaDao geographicAreaDao;

    @Autowired
    GeographicAreaCustomDao geographicAreaCustomDao;

    @Override
    public List<GeographicArea> searchGeographicAreaIntersections(Geometry geometry, GeographicType geographicType, Long idContext, Long idRole) {

        // On cherche d'abord l'id de la geographic Area
        List<GeographicAreaEntity> geographicAreaEntities = geographicAreaCustumDao.searchGeographicAreaIntersections(geometry, geographicType, idContext, idRole);
        // On convertie les entités en dtos
        return geographicAreaMapper.entitiesToDtos(geographicAreaEntities);
    }

    @Override
    public Page<GeographicArea> searchGeographicAreas(String name, Pageable pageable) {

        GeographicAreaSearchCriteria searchCriteria = new GeographicAreaSearchCriteria();
        SortCriteria sort = new SortCriteria();

        if(StringUtils.isNotEmpty(name)){
            searchCriteria.setNom(name);
        }

        Page<GeographicAreaEntity> geographicAreas;
        geographicAreas = geographicAreaCustomDao
                .searchGeographicAreas(searchCriteria,pageable, sort);
        return geographicAreas.map(geographicAreaMapper::entityToDto);
    }

    @Override
    public GeographicArea getGeographicArea(Long id) {
        return geographicAreaMapper.entityToDto(geographicAreaDao.findEntityById(id));
    }
}
