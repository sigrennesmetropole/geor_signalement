package org.georchestra.signalement.service.acl.impl;

import com.vividsolutions.jts.geom.Geometry;
import org.georchestra.signalement.core.dao.acl.GeographicAreaCustumDao;
import org.georchestra.signalement.core.dto.GeographicArea;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.georchestra.signalement.service.acl.GeographicAreaService;
import org.georchestra.signalement.service.mapper.acl.GeographicAreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeographicAreaServiceImpl implements GeographicAreaService {

    @Autowired
    private GeographicAreaCustumDao geographicAreaCustumDao;

    @Autowired
    private GeographicAreaMapper geographicAreaMapper;

    @Override
    public List<GeographicArea> searchGeographicAreaIntersectWithGeometryRectrictedOnRoleAndContext(Geometry geometry, GeographicType geographicType, Long idContext, Long idRole) {

        // On cherche d'abord l'id de la geographic Area
        List<GeographicAreaEntity> geographicAreaEntities = geographicAreaCustumDao.searchGeographicAreaIntersectWithGeometryRectrictedOnRoleAndContext(geometry, geographicType, idContext, idRole);
        // On convertie les entités en dtos
        return geographicAreaMapper.entitiesToDtos(geographicAreaEntities);
    }
}
