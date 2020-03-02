package org.georchestra.signalement.core.dao.form;

import java.util.List;

import org.georchestra.signalement.core.dto.ProcessFormDefinitionSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.entity.form.ProcessFormDefinitionEntity;

/**
 * @author FNI18300
 *
 */
public interface ProcessFormDefinitionCustomDao {

	/**
	 * 
	 * @param searchCriteria
	 * @param sortCriteria
	 * @return
	 */
	List<ProcessFormDefinitionEntity> searchProcessFormDefintions(ProcessFormDefinitionSearchCriteria searchCriteria,
			SortCriteria sortCriteria);

}
