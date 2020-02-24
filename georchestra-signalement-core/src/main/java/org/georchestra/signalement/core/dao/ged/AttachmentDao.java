/**
 * 
 */
package org.georchestra.signalement.core.dao.ged;

import java.util.List;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.entity.ged.AttachmentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author FNI18300
 *
 */
@Repository
public interface AttachmentDao extends QueryDslDao<AttachmentEntity, Long> {

	@Query(value = "select a from AttachmentEntity a, IN(a.attachmentIds) as attachmentIds where attachmentIds = :attachmentId ")
	List<AttachmentEntity> findByAttachmentIds(@Param("attachmentId") String attachmentId);

}
