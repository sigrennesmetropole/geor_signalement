/**
 * 
 */
package org.georchestra.signalement.core.dao.form;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.entity.form.SectionDefinitionEntity;
import org.springframework.stereotype.Repository;

/**
 * @author FNI18300
 *
 */
@Repository
public interface SectionDefintionDao extends QueryDslDao<SectionDefinitionEntity, Long> {

}
