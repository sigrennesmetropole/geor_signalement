package org.georchestra.signalement.core.dao.acl;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.entity.acl.UserRoleContextEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleContextDao extends QueryDslDao<UserRoleContextEntity, Long> {
}
