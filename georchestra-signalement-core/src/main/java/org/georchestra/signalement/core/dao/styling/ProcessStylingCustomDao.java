/**
 * 
 */
package org.georchestra.signalement.core.dao.styling;

import java.util.List;

import org.georchestra.signalement.core.dto.ProcessStylingSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.entity.styling.ProcessStylingEntity;

/**
 * @author FNI18300
 *
 */
public interface ProcessStylingCustomDao {
	/**
	 * 
	 * @param searchCriteria
	 * @param sortCriteria
	 * @return
	 */
	List<ProcessStylingEntity> searchProcessStylings(ProcessStylingSearchCriteria searchCriteria,
			SortCriteria sortCriteria);
	
	/**
	 * @param searchCriteria
	 * @return le plus proche
	 */
	ProcessStylingEntity findBestProcessStyling(ProcessStylingSearchCriteria searchCriteria);


	void deleteByStylingId(long aLong);
}
