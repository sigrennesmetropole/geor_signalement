/**
 * 
 */
package org.georchestra.signalement.core.dao.acl;

import java.util.List;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.dto.ContextType;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.springframework.stereotype.Repository;

/**
 * @author FNI18300
 *
 */
@Repository
public interface ContextDescriptionDao extends QueryDslDao<ContextDescriptionEntity, Long> {

	List<ContextDescriptionEntity> findByContextType(ContextType contextType);

	ContextDescriptionEntity findByName(String name);

}
