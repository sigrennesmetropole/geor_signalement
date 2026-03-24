/**
 *
 */
package org.georchestra.signalement.core.dao.reporting;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.dto.Status;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.springframework.stereotype.Repository;

/**
 * @author FNI18300
 *
 */
@Repository
public interface ReportingDao extends QueryDslDao<AbstractReportingEntity, Long> {

	AbstractReportingEntity findByUuid(UUID uuid);

	/**
	 * Retourne la liste des signalements dont le statut correspond et dont la date de création est antérieure à une date donnée
	 *
	 * @param status       le statut cible
	 * @param creationDate la date limite
	 * @return la liste des signalements correspondants
	 */
	List<AbstractReportingEntity> findByStatusAndCreationDateBefore(Status status, Date creationDate);

}
