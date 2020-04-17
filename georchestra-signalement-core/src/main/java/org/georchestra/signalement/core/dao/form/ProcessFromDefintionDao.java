/**
 * 
 */
package org.georchestra.signalement.core.dao.form;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.entity.form.ProcessFormDefinitionEntity;
import org.springframework.stereotype.Repository;

/**
 * @author FNI18300
 *
 */
@Repository
public interface ProcessFromDefintionDao extends QueryDslDao<ProcessFormDefinitionEntity, Long> {

}
