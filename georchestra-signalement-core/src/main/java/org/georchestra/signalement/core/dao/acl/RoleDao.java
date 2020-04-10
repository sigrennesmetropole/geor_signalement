package org.georchestra.signalement.core.dao.acl;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.entity.acl.RoleEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao  extends QueryDslDao<RoleEntity, Long> {

    RoleEntity findByName(String name);

}
