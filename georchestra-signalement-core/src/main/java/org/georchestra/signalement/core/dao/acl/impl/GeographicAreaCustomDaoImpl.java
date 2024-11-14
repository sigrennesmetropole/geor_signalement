package org.georchestra.signalement.core.dao.acl.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.AbstractCustomDaoImpl;
import org.georchestra.signalement.core.dao.acl.GeographicAreaCustomDao;
import org.georchestra.signalement.core.dto.GeographicAreaSearchCriteria;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class GeographicAreaCustomDaoImpl extends AbstractCustomDaoImpl implements GeographicAreaCustomDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextDescriptionCustomDaoImpl.class);

    private static final String NOM = "nom";
    @Autowired
    private EntityManager entityManager;

    @Override
    protected Map<String, Path<?>> addJoinSortCriteria(CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<?> root, SortCriteria sortCriteria) {
        return null;
    }

    @Override
    public List<GeographicAreaEntity> searchGeographicAreaIntersections(Geometry geometry, GeographicType geographicType, Long idContext, Long idRole) {
        String sqlQuery = null;
        switch (geographicType) {
            case POINT:
                sqlQuery = String.format("select distinct g.id, g.nom, g.codeinsee, g.geometry " +
                        "from geographic_area g inner join user_role_context urc on g.id=urc.geographic_area_id " +
                        "where st_contains(g.geometry, ST_GeometryFromText('%1$s',4326)) = TRUE " +
                        "and urc.context_description_id = %2$d " +
                        "and urc.role_id = %3$d;", geometry, idContext, idRole);
                break;
            case LINE:
                sqlQuery = String.format("select distinct g.id, g.nom, g.codeinsee, g.geometry, st_length(ST_Intersection(geography('%1$s') ,g.geometry)) as longueur " +
                        "from geographic_area g inner join user_role_context urc on g.id=urc.geographic_area_id " +
                        "where ST_Intersects(geography('%1$s'), g.geometry) = TRUE " +
                        "and urc.context_description_id = %2$d " +
                        "and urc.role_id = %3$d " +
                        "order by longueur desc;", geometry, idContext, idRole);
                break;
            case POLYGON:
                sqlQuery = String.format("select distinct g.id, g.nom, g.codeinsee, g.geometry, st_area(st_intersection(geography('%1$s') ,g.geometry)) as area " +
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

    @Override
    public Page<GeographicAreaEntity> searchGeographicAreas(GeographicAreaSearchCriteria searchCriteria, Pageable pageable, SortCriteria sortCriteria) {
        List<GeographicAreaEntity> results = null;

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<GeographicAreaEntity> searchQuery = builder.createQuery(GeographicAreaEntity.class);
        Root<GeographicAreaEntity> searchRoot = searchQuery.from(GeographicAreaEntity.class);

        buildQuery(searchCriteria, builder, searchQuery, searchRoot);
        applySortCriteria(builder, searchQuery, searchRoot, sortCriteria);

        TypedQuery<GeographicAreaEntity> typedQuery = entityManager.createQuery(searchQuery);
        results = typedQuery.getResultList();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("{} results founded", results.size());
        }
        return new PageImpl<>(results, pageable, results.size());
    }

    private void buildQuery(GeographicAreaSearchCriteria searchCriteria, CriteriaBuilder builder, CriteriaQuery<GeographicAreaEntity> searchQuery, Root<GeographicAreaEntity> root) {

        if (searchCriteria != null) {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotEmpty(searchCriteria.getNom())) {
                if (isWildCarded(searchCriteria.getNom())) {
                    predicates.add(
                            builder.like(builder.lower(root.get(NOM)), wildcard(searchCriteria.getNom())));
                } else {
                    predicates.add(builder.equal(root.get(NOM), searchCriteria.getNom()));
                }
            }
            if (CollectionUtils.isNotEmpty(predicates)) {
                searchQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
            }
        }
    }
}

