package org.georchestra.signalement.service.acl.impl;

import com.vividsolutions.jts.geom.Geometry;
import org.georchestra.signalement.core.dao.acl.GeographicAreaCustumDao;
import org.georchestra.signalement.core.dto.GeographicArea;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.georchestra.signalement.service.acl.GeographicAreaService;
import org.georchestra.signalement.service.mapper.acl.GeographicAreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeographicAreaServiceImpl implements GeographicAreaService {

    @Value("${geographic_area.rennes_metropole}")
    private String rennesMetropole;

    @Autowired
    private GeographicAreaCustumDao geographicAreaCustumDao;

    @Autowired
    private GeographicAreaMapper geographicAreaMapper;

    @Override
    public List<GeographicArea> searchGeographicAreaIntersectWithGeometry(Geometry geometry, GeographicType geographicType) {
        // On cherche d'abord l'id de la geographic Area
        List<GeographicAreaEntity> geographicAreaEntities = geographicAreaCustumDao.searchGeographicAreaIntersectWithGeometry(geometry, geographicType, rennesMetropole);
        // On convertie les entités en dtos
        return geographicAreaMapper.entitiesToDtos(geographicAreaEntities);
    }
}
