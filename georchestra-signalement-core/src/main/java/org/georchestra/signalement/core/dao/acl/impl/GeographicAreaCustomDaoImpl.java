package org.georchestra.signalement.core.dao.acl.impl;

import com.vividsolutions.jts.geom.Geometry;

import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.acl.GeographicAreaCustumDao;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

@Repository
public class GeographicAreaCustomDaoImpl extends AbstractCustomDaoImpl implements GeographicAreaCustumDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    protected Map<String, Path<?>> addJoinSortCriteria(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<?> root, SortCriteria sortCriteria) {
        return null;
    }

    /**
     * Récuperer le geographicArea qui intersecte la géometrie.
     * Dans le cas des polygones ou lignes à cheval sur plusieurs polygones,
     * on les classespar les polygones qui ont la plus grande surface ou la plus grande ligne d'intersection
     *
     * @param geometry
     * @param geographicType
     * @return
     */
    @Override
    public List<GeographicAreaEntity> searchGeographicAreaIntersectWithGeometry(Geometry geometry, GeographicType geographicType, String excludedArea) {
        String sqlQuery = null;
        switch (geographicType) {
            case POINT:
                sqlQuery = String.format("select *  " +
                        "from geographic_area g " +
                        "where st_contains(g.geometry, ST_GeometryFromText('%s',4326)) = TRUE and nom <> '%s';", geometry, excludedArea);
                break;
            case LINE:
                sqlQuery = String.format("select id, nom, codeinsee, geometry from (select *, st_length(ST_Intersection(geography('%1$s') ,g.geometry)) as longueur " +
                        "from geographic_area g " +
                        "where ST_Intersects(geography('%1$s'), g.geometry) = TRUE " +
                        "and nom <> '%2$s' " +
                        "order by longueur desc) AS g_len;", geometry, excludedArea);
                break;
            case POLYGON:
                sqlQuery = String.format("select id, nom, codeinsee, geometry from (select *, st_area(st_intersection(geography('%1$s') ,g.geometry)) as area " +
                        "from geographic_area g " +
                        "where ST_Intersects(geography('%1$s'), g.geometry) = TRUE " +
                        "and nom <> '%2$s' " +
                        "order by area desc) AS g_area;", geometry, excludedArea);
                break;
            default:
                break;
        }

        //Création de la requête
        Query query = entityManager.createNativeQuery(sqlQuery, GeographicAreaEntity.class);

        //Execution de la requête
        @SuppressWarnings("unchecked")
		List<GeographicAreaEntity> result = query.getResultList();
        return result;


    }


}

