package org.georchestra.signalement.core.dao.acl;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface GeographicAreaDao extends QueryDslDao<GeographicAreaEntity, Long> {
    GeographicAreaEntity findEntityById(Long id);
}
