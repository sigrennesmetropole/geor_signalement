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

    @Override
    public List<GeographicAreaEntity> searchGeographicAreaIntersectWithGeometryRectrictedOnRoleAndContext(Geometry geometry, GeographicType geographicType, Long idContext, Long idRole) {
        String sqlQuery = null;
        switch (geographicType) {
            case POINT:
                sqlQuery = String.format("select g.id, g.nom, g.codeinsee, g.geometry " +
                        "from geographic_area g inner join user_role_context urc on g.id=urc.geographic_area_id " +
                        "where st_contains(g.geometry, ST_GeometryFromText('%1$s',4326)) = TRUE " +
                        "and urc.context_description_id = %2$d " +
                        "and urc.role_id = %3$d;", geometry, idContext, idRole);
                break;
            case LINE:
                sqlQuery = String.format("select g.id, g.nom, g.codeinsee, g.geometry, st_length(ST_Intersection(geography('%1$s') ,g.geometry)) as longueur " +
                        "from geographic_area g inner join user_role_context urc on g.id=urc.geographic_area_id " +
                        "where ST_Intersects(geography('%1$s'), g.geometry) = TRUE " +
                        "and urc.context_description_id = %2$d " +
                        "and urc.role_id = %3$d " +
                        "order by longueur desc;", geometry, idContext, idRole);
                break;
            case POLYGON:
                sqlQuery = String.format("select g.id, g.nom, g.codeinsee, g.geometry, st_area(st_intersection(geography('%1$s') ,g.geometry)) as area " +
                        "from geographic_area g inner join user_role_context urc on g.id=urc.geographic_area_id " +
                        "where ST_Intersects(geography('%1$s'), g.geometry) = TRUE " +
                        "and urc.context_description_id = %2$d " +
                        "and urc.role_id = %3$d " +
                        "order by area desc;", geometry, idContext, idRole);
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

