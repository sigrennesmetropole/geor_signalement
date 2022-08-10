package org.georchestra.signalement.core.dao.styling;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.entity.styling.ProcessStylingEntity;
import org.georchestra.signalement.core.entity.styling.StylingEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessStylingDao extends QueryDslDao<ProcessStylingEntity, Long> {
    List<ProcessStylingEntity> findByStylingId(long id);

    ProcessStylingEntity findById(long id);
}
