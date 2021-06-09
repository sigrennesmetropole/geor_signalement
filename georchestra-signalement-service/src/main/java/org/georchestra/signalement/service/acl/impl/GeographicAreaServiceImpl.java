package org.georchestra.signalement.service.acl.impl;

import com.vividsolutions.jts.geom.Geometry;
import org.georchestra.signalement.core.dao.acl.GeographicAreaCustumDao;
import org.georchestra.signalement.core.dao.acl.GeographicAreaDao;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.georchestra.signalement.service.acl.GeographicAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeographicAreaServiceImpl implements GeographicAreaService {
    @Autowired
    private GeographicAreaCustumDao geographicAreaCustumDao;

    @Autowired
    private GeographicAreaDao geographicAreaDao;

    @Override
    public String getNomGeographicAreaIntersectWithGeometry(Geometry geometry, GeographicType geographicType) {
        // On cherche d'abord l'id de la geographic Area
        Long id = geographicAreaCustumDao.findGeographicAreaIntersectWithGeometry(geometry, geographicType);

        // On recupere la geographicArea à partir de son id
        GeographicAreaEntity geographicAreaEntity = null;
        if (id != null) {
            geographicAreaEntity = this.geographicAreaDao.findById(id).orElse(null);
        }
        return (geographicAreaEntity != null) ? geographicAreaEntity.getNom() : null;
    }
}
