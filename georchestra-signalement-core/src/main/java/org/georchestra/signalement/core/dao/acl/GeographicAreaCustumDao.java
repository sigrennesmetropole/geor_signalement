package org.georchestra.signalement.core.dao.acl;


import com.vividsolutions.jts.geom.Geometry;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;

import java.util.List;


public interface GeographicAreaCustumDao {

    /**
     *  Récuperer l'id d'intersection entre la geometrie et la table geographic area
     * @param geometry          Géometrie representant le signalement
     * @param geographicType    Type de géometrie fournie (ligne, point, polygone)
     * @param excludedArea      geographic area a exclure des resuktats de la recherche (en l'occurrence Rennes Métropole)
     * @return
     */
    public List<GeographicAreaEntity> searchGeographicAreaIntersectWithGeometry(Geometry geometry, GeographicType geographicType, String excludedArea);
}
