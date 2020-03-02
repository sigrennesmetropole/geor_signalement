/**
 * 
 */
package org.georchestra.signalement.service.sm.impl;

import java.util.List;

import org.georchestra.signalement.core.dao.acl.ContextDescriptionCustomDao;
import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.ContextDescriptionSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.service.mapper.acl.ContextDescriptionMapper;
import org.georchestra.signalement.service.sm.ContextDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 *
 */
@Service
public class ContextDescriptionServiceImpl implements ContextDescriptionService {

	@Autowired
	private ContextDescriptionCustomDao contextDescriptionCustomDao;

	@Autowired
	private ContextDescriptionMapper contextDescriptionMapper;

	@Override
	public List<ContextDescription> searchContextDescriptions(ContextDescriptionSearchCriteria searchCriteria,
			SortCriteria sortCriteria) {

		return contextDescriptionMapper
				.entitiesToDtos(contextDescriptionCustomDao.searchContextDescriptions(searchCriteria, sortCriteria));
	}

}
