package org.georchestra.signalement.service.acl;

import java.util.List;

import org.georchestra.signalement.core.dto.GeographicArea;
import org.georchestra.signalement.core.dto.GeographicType;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service des geographic area
 */
public interface GeographicAreaService {
    /**
     * Permet de retourner la liste des geographicArea auquelles appartient une geometrie donnée.
     *
     * @param geometry       geometrie dont on veut déterminer les geographicArea d'appartenance
     * @param geographicType le type de la geomtrie (point, line, polygon)
     * @return {string} le nom de la geograhicARea
     */
    List<GeographicArea> searchGeographicAreaIntersections(Geometry geometry, GeographicType geographicType, Long idContext, Long idRole);

    /**
     * Méthode paginée de recherche des zones géographiques
     * @param name
     * @param pageable
     * @return
     */
    Page<GeographicArea> searchGeographicAreas(String name, Pageable pageable);

    /**
     * Récupération unitaire d'une zone géographique
     * @param id
     * @return
     */
    GeographicArea getGeographicArea(Long id);
}
