package org.georchestra.signalement.core.dao.acl;


import com.vividsolutions.jts.geom.Geometry;
import org.georchestra.signalement.core.dto.GeographicType;


public interface GeographicAreaCustumDao {

    /**
     *  Récuperer l'id d'intersection entre la geometrie et la table geographic area
     * @param geometry
     * @param geographicType
     * @return
     */
    Long findGeographicAreaIntersectWithGeometry(Geometry geometry, GeographicType geographicType);
}
