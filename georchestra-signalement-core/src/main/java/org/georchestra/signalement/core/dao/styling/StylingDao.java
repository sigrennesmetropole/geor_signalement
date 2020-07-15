package org.georchestra.signalement.core.dao.styling;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.entity.styling.StylingEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface StylingDao  extends QueryDslDao<StylingEntity, Long> {

	StylingEntity findByName(String name);

}
