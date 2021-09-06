package org.georchestra.signalement.core.dao.acl;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GeographicAreaDao extends QueryDslDao<GeographicAreaEntity, Long> {

    @Query(value = "SELECT ag FROM GeographicAreaEntity ag WHERE ag.nom = :name")
    GeographicAreaEntity findByName(@Param("name") String name);

}