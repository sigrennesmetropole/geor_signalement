package org.georchestra.signalement.service.acl;

import com.vividsolutions.jts.geom.Geometry;
import org.georchestra.signalement.core.dto.GeographicType;

/**
 * Service des geographic area
 */
public interface GeographicAreaService {
    /**
     * Permet de retourner le nom de la commune ou geographicArea à laquelle appartient une geometrie donnée.
     * @param geometry          geometrie dont on veut déterminer la geographicArea d'appartenance
     * @param geographicType    le type de la geomtrie (point, line, polygon)
     * @return {string} le nom de la geograohicARea
     */
    String getNomGeographicAreaIntersectWithGeometry(Geometry geometry, GeographicType geographicType);
}
