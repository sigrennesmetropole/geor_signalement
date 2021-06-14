package org.georchestra.signalement.service.acl;

import com.vividsolutions.jts.geom.Geometry;
import org.georchestra.signalement.core.dto.GeographicArea;
import org.georchestra.signalement.core.dto.GeographicType;

import java.util.List;

/**
 * Service des geographic area
 */
public interface GeographicAreaService {
    /**
     * Permet de retourner la liste des geographicArea auquelles appartient une geometrie donnée.
     * @param geometry          geometrie dont on veut déterminer les geographicArea d'appartenance
     * @param geographicType    le type de la geomtrie (point, line, polygon)
     * @return {string} le nom de la geograhicARea
     */
    List<GeographicArea> searchGeographicAreaIntersectWithGeometry(Geometry geometry, GeographicType geographicType);
}
