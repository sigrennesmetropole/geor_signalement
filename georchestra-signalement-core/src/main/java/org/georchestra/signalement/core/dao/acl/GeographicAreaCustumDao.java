package org.georchestra.signalement.core.dao.acl;


import com.vividsolutions.jts.geom.Geometry;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;

import java.util.List;


public interface GeographicAreaCustumDao {

    /**
     *  Récuperer l'id d'intersection entre la geometrie et la table geographic area
     * @param geometry
     * @param geographicType
     * @return
     */
    public List<GeographicAreaEntity> searchGeographicAreaIntersectWithGeometry(Geometry geometry, GeographicType geographicType);
}
