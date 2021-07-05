package org.georchestra.signalement.core.dao.acl;


import com.vividsolutions.jts.geom.Geometry;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;

import java.util.List;


public interface GeographicAreaCustumDao {

    /**
     *  Récuperer les geographic area d'intersection entre la geometrie et la table geographic area et du contexte et du role concerné
     * @param geometry          Géometrie representant le signalement
     * @param geographicType    Type de géometrie fournie (ligne, point, polygone)
     * @param idContext         Id du contexte, restrictions sur les geographic area appartenant au contexte
     * @param idRole                         Role des utilisateurs du contexte qui sont concernés
     * @return
     */
    public List<GeographicAreaEntity> searchGeographicAreaIntersectWithGeometryRectrictedOnRoleAndContext(Geometry geometry, GeographicType geographicType, Long idContext, Long idRole);
}
