/**
 * 
 */
package org.georchestra.signalement.service.sm.impl;

import java.util.List;
import java.util.UUID;

import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dao.reporting.ReportingDao;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.service.helper.reporting.ReportingHelper;
import org.georchestra.signalement.service.mapper.ContextDescriptionMapper;
import org.georchestra.signalement.service.sm.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class TaskServiceImpl implements TaskService {

	@Autowired
	private ReportingDao reportingDao;

	@Autowired
	private ContextDescriptionDao contextDescriptionDao;

	@Autowired
	private ReportingHelper reportingHelper;

//	@Autowired
//	private ReportingMapper reporting;

	@Override
	public Task claimTask(String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task createDraft(ReportingDescription reportingDescription) {
		if (reportingDescription == null || reportingDescription.getContext() == null) {
			throw new IllegalArgumentException("Reporting with a context is mandatory");
		}
		ContextDescriptionEntity contextDescriptionEntity = contextDescriptionDao
				.findByName(reportingDescription.getContext().getName());
		if (contextDescriptionEntity == null) {
			throw new IllegalArgumentException("Invalid context name:" + reportingDescription.getContext().getName());
		}
		AbstractReportingEntity reportingEntity = reportingHelper
				.createReportingEntity(reportingDescription.getContext().getGeographicType());

		return null;
	}

	@Override
	public void doIt(String taskId, String actionName) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Task> searchTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task startTask(Task task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAttachment(UUID reportingUuid, DocumentContent content) {
		// TODO Auto-generated method stub

	}

	@Override
	public DocumentContent getAttachment(UUID reportingUuid, String attachmentId) {
		// TODO Auto-generated method stub
		return null;
	}

}
