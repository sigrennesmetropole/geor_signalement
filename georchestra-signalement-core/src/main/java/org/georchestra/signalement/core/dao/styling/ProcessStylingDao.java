package org.georchestra.signalement.core.dao.styling;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.entity.styling.ProcessStylingEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessStylingDao extends QueryDslDao<ProcessStylingEntity, Long> {

}
