package org.georchestra.signalement.core.dao.acl.impl;

import com.vividsolutions.jts.geom.Geometry;
import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.acl.GeographicAreaCustumDao;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.SortCriteria;
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
public class GeographicAreaCustomDapImpl extends AbstractCustomDaoImpl implements GeographicAreaCustumDao {

    @Autowired
    private EntityManager entityManager;

    @Override
    protected Map<String, Path<?>> addJoinSortCriteria(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<?> root, SortCriteria sortCriteria) {
        return null;
    }

    /** Récuperer l'id du polygone de la table geographicArea intersecter avec la géometrie.
     * Dans le cas des polygones ou lignes à cheval sur plusieurs polygones,
     * on récupere l'id du polygone qui la plus grande surface ou la plus grande ligne d'intersection
     *
     * @param geometry
     * @param geographicType
     * @return
     */
    @Override
    public Long findGeographicAreaIntersectWithGeometry(Geometry geometry, GeographicType geographicType) {
        String sqlQuery = null;
        switch (geographicType) {
            case POINT:
                sqlQuery = String.format("select g.id  " +
                        "from geographic_area g " +
                        "where st_contains(g.geom, geography('%s')) = TRUE;", geometry);
                break;
            case LINE:
                sqlQuery = String.format("select g.id, st_length(ST_Intersection(geography('%1$s') ,g.geom)) as longeur " +
                        "from geographic_area g " +
                        "where ST_Intersects(geography('%1$s'), g.geom) = TRUE "  +
                        "order by longeur desc " +
                        "limit 1; ", geometry);
                break;
            case POLYGON:
                sqlQuery = String.format("select g.id, st_area(st_intersection(geography('%1$s') ,g.geom)) as area " +
                        "from geographic_area g " +
                        "where ST_Intersects(geography('%1$s'), g.geom) = TRUE " +
                        "order by area desc " +
                        "limit 1; ", geometry);
                break;
            default:
                break;
        }

        //Création de la requête
        Query query = entityManager.createNativeQuery(sqlQuery);

        //Execution de la requête
        List<Object[]> result = query.getResultList();

        // Dans le cas où y a pas de resultat on retourne null sinon on retourne l'id du polygone de la geographicArea
        if(result.isEmpty()){
            return null;
        }else{
            Integer idGeographicArea = (Integer) result.get(0)[0];
            return Long.valueOf(idGeographicArea);
        }


    }
}
