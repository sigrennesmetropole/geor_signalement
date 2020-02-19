/**
 * 
 */
package org.georchestra.signalement.core.dao.reporting;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.springframework.stereotype.Repository;

/**
 * @author FNI18300
 *
 */
@Repository
public interface ReportingDao extends QueryDslDao<AbstractReportingEntity, Long> {

}
